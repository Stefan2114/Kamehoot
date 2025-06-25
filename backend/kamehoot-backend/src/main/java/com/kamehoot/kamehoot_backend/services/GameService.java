package com.kamehoot.kamehoot_backend.services;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamehoot.kamehoot_backend.DTOs.GameQuestionDTO;
import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;
import com.kamehoot.kamehoot_backend.DTOs.PlayerDTO;
import com.kamehoot.kamehoot_backend.DTOs.WebSocketDTO;
import com.kamehoot.kamehoot_backend.enums.GameStatus;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.GameAnswer;
import com.kamehoot.kamehoot_backend.models.GamePlayer;
import com.kamehoot.kamehoot_backend.models.GameSession;
import com.kamehoot.kamehoot_backend.models.Quiz;
import com.kamehoot.kamehoot_backend.models.QuizQuestion;
import com.kamehoot.kamehoot_backend.repos.IGameAnswerRepository;
import com.kamehoot.kamehoot_backend.repos.IGamePlayerRepository;
import com.kamehoot.kamehoot_backend.repos.IGameSessionRepository;
import com.kamehoot.kamehoot_backend.repos.IQuizRepository;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;

@Service
@Transactional
public class GameService implements IGameService {

    private final IGameSessionRepository gameSessionRepository;
    private final IGamePlayerRepository gamePlayerRepository;
    private final IGameAnswerRepository gameAnswerRepository;
    private final IQuizRepository quizRepository;
    private final IUserRepository userRepository;

    private final ConcurrentHashMap<UUID, Set<WebSocketSession>> activeGames = new ConcurrentHashMap<>();
    private final Map<String, UUID> sessionToGame = new ConcurrentHashMap<>();
    private final Map<UUID, LocalDateTime> questionStartTimes = new ConcurrentHashMap<>();
    private final Map<String, UUID> codeToGameIds = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> gamesCurrentQuestionIndex = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameService(IGameSessionRepository gameSessionRepository,
            IGamePlayerRepository gamePlayerRepository,
            IGameAnswerRepository gameAnswerRepository,
            IQuizRepository quizRepository,
            IUserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gameAnswerRepository = gameAnswerRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void connect(UUID gameSessionId, WebSocketSession session) {
        Set<WebSocketSession> sessions = this.activeGames.get(gameSessionId);
        sessions.add(session);
        sessionToGame.put(session.getId(), gameSessionId);
    }

    @Override
    public void disconnect(WebSocketSession session) {

        UUID gameSessionId = sessionToGame.get(session.getId());
        if (gameSessionId != null) {
            Set<WebSocketSession> sessions = activeGames.get(gameSessionId);
            sessions.remove(session);
            if (sessions.isEmpty()) {
                activeGames.remove(gameSessionId);
            }

        }
        sessionToGame.remove(session.getId());
    }

    @Override
    public String createGame(String hostUsername, UUID quizId, Integer timeLimit) {
        AppUser host = userRepository.findByUsername(hostUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        GameSession gameSession = new GameSession();
        gameSession.setQuiz(quiz);
        gameSession.setHost(host);

        gameSession.setCreatedAt(LocalDateTime.now());
        gameSession.setQuestionTimeLimit(timeLimit != null ? timeLimit : 15);
        gameSession.setStatus(GameStatus.WAITING);
        UUID gameSessionId = gameSessionRepository.save(gameSession).getId();
        String gameCode = this.generateGameCode();

        this.codeToGameIds.put(gameCode, gameSessionId);

        activeGames.put(gameSessionId, new ConcurrentSkipListSet<>());

        return gameCode;
    }

    @Override
    public void startGame(String hostUsername, UUID gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!gameSession.getHost().getUsername().equals(hostUsername)) {
            throw new RuntimeException("Only host can start game");
        }

        if (gameSession.getPlayers().isEmpty()) {
            throw new RuntimeException("Need at least 1 player");
        }

        gameSession.setStatus(GameStatus.IN_PROGRESS);
        gameSession.setStartedAt(LocalDateTime.now());

        gameSessionRepository.save(gameSession);

        this.gamesCurrentQuestionIndex.put(gameSessionId, 0);

        // Start first question immediately - no scheduling
        startQuestion(gameSession);
    }

    private void startQuestion(GameSession gameSession) {

        UUID gameSessionId = gameSession.getId();
        QuizQuestion currentQuestion = getCurrentQuestion(gameSession);
        LocalDateTime startTime = LocalDateTime.now();
        questionStartTimes.put(gameSessionId, startTime);

        // Prepare shuffled options
        List<String> options = new ArrayList<>();
        options.add(currentQuestion.getQuestion().getCorrectAnswer());
        options.addAll(currentQuestion.getQuestion().getWrongAnswers());
        Collections.shuffle(options);

        Set<WebSocketSession> sessions = this.activeGames.get(gameSessionId);

        GameQuestionDTO gameQuestionDTO = new GameQuestionDTO(currentQuestion.getId(),
                currentQuestion.getQuestion().getQuestionText(), options,
                this.gamesCurrentQuestionIndex.get(gameSession.getId()), startTime, gameSession.getQuestionTimeLimit());
        try {
            String messageJson = objectMapper.writeValueAsString(new WebSocketDTO("gameQuestion", gameQuestionDTO));
            synchronized (sessions) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageJson));
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("Error parsing the players to json " + exception.getMessage());
            throw new RuntimeException("Error parsing to json");
        }

    }

    private QuizQuestion getCurrentQuestion(GameSession gameSession) {
        List<QuizQuestion> questions = gameSession.getQuiz().getQuestions();
        Integer currentQuestionIndex = this.gamesCurrentQuestionIndex.get(gameSession.getId());
        if (currentQuestionIndex >= questions.size()) {
            throw new RuntimeException("No more questions");
        }
        return questions.get(currentQuestionIndex);
    }

    @Override
    public void joinGame(String username, String gameCode) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID gameSessionId = this.codeToGameIds.get(gameCode);

        if (gameSessionId == null) {
            throw new RuntimeException("No game found for the given gameCode");
        }

        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (gameSession.getStatus() != GameStatus.WAITING) {
            throw new RuntimeException("Game already started");
        }

        boolean alreadyJoined = gameSession.getPlayers().stream()
                .anyMatch(player -> player.getUser().getUsername().equals(username));

        if (alreadyJoined) {
            throw new RuntimeException("Already in game");
        }

        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setGameSession(gameSession);
        gamePlayer.setUser(user);

        gamePlayerRepository.save(gamePlayer);
        gameSession.getPlayers().add(gamePlayer);

        Set<WebSocketSession> sessions = this.activeGames.get(gameSessionId);
        List<String> players = gameSession.getPlayers().stream().map(player -> player.getUser().getUsername()).toList();
        try {
            String messageJson = objectMapper.writeValueAsString(new WebSocketDTO("playersJoined", players));
            synchronized (sessions) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageJson));
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("Error parsing the players to json " + exception.getMessage());
            throw new RuntimeException("Error parsing to json");
        }

    }

    @Override
    public void submitAnswer(String username, UUID gameSessionId, UUID questionId, String answer,
            LocalDateTime answerTime) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (gameSession.getStatus() != GameStatus.IN_PROGRESS) {
            throw new RuntimeException("Game not active");
        }

        GamePlayer player = findPlayer(gameSession, username);
        QuizQuestion currentQuestion = getCurrentQuestion(gameSession);

        if (!currentQuestion.getQuestion().getId().equals(questionId)) {
            throw new RuntimeException("Wrong question");
        }

        if (hasAnswered(player, questionId)) {
            throw new RuntimeException("Already answered");
        }

        LocalDateTime startTime = this.questionStartTimes.get(gameSession.getId());
        long responseTime = Duration.between(startTime, answerTime).toMillis();

        boolean isCorrect = currentQuestion.getQuestion().getCorrectAnswer().equals(answer);
        int points = isCorrect ? calculatePoints(responseTime, gameSession.getQuestionTimeLimit()) : 0;

        // Save answer
        GameAnswer gameAnswer = new GameAnswer();
        gameAnswer.setGamePlayer(player);
        gameAnswer.setQuizQuestion(currentQuestion);
        gameAnswer.setUserAnswer(answer);
        gameAnswer.setIsCorrect(isCorrect);
        gameAnswer.setAnsweredAt(LocalDateTime.now());
        gameAnswer.setResponseTime(responseTime);
        gameAnswer.setPointsEarned(points);

        gameAnswerRepository.save(gameAnswer);

        Set<WebSocketSession> sessions = this.activeGames.get(gameSessionId);

        try {
            String messageJson = "";
            Integer playersAnswered = this.gameAnswerRepository
                    .getAllByGameSessionIdAndQuestionId(gameSessionId, questionId).size();
            if (playersAnswered < gameSession.getPlayers().size()) {
                messageJson = objectMapper.writeValueAsString(
                        new WebSocketDTO("playersAnswered", playersAnswered));

            } else {
                messageJson = objectMapper.writeValueAsString(
                        new WebSocketDTO("leaderboard", getSimpleLeaderboard(gameSession, questionId)));

            }
            synchronized (sessions) {
                for (WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageJson));
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("Error parsing the players to json " + exception.getMessage());
            throw new RuntimeException("Error parsing to json");
        }

    }

    private int calculatePoints(long responseTimeMs, int timeLimitSeconds) {
        long timeLimitMs = timeLimitSeconds * 1000L;
        if (responseTimeMs >= timeLimitMs)
            return 0;

        double speedFactor = 1.0 - (responseTimeMs / (double) timeLimitMs);
        return 100 + (int) (900 * speedFactor);
    }

    @Override
    public void nextQuestion(String hostUsername, UUID gameSessionId) {
        GameSession gameSession = this.gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!gameSession.getHost().getUsername().equals(hostUsername)) {
            throw new RuntimeException("Only host can advance");
        }
        Integer currentQuestionIndex = this.gamesCurrentQuestionIndex.get(gameSession.getId());

        this.gamesCurrentQuestionIndex.put(gameSessionId, currentQuestionIndex + 1);
        startQuestion(gameSession);

    }

    private GamePlayer findPlayer(GameSession gameSession, String username) {
        for (GamePlayer player : gameSession.getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                return player;
            }
        }
        throw new RuntimeException("Player not in game");
    }

    private List<PlayerDTO> getSimpleLeaderboard(GameSession gameSession, UUID questionId) {

        return gameSession.getPlayers().stream().map(gamePlayer -> new PlayerDTO(gamePlayer.getUser().getUsername(),
                gamePlayer.getTotalScore(), hasAnswered(gamePlayer, questionId)))
                .sorted((a, b) -> b.totalScore().compareTo(a.totalScore())).toList();
    }

    private boolean hasAnswered(GamePlayer player, UUID questionId) {

        return player.getAnswers().stream().anyMatch(answer -> answer.getQuizQuestion().getId().equals(questionId));
    }

    private String generateGameCode() {
        String code;
        do {
            code = String.format("%06d", new Random().nextInt(1000000));
        } while (this.codeToGameIds.containsKey(code));
        return code;
    }

    @Override
    public GameSessionDTO getGameSessionDTO(String gameCode) {
        UUID gameSessionId = this.codeToGameIds.get(gameCode);

        if (gameSessionId == null) {
            throw new RuntimeException("No game found for the given gameCode");
        }

        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        return new GameSessionDTO(gameSession.getId(), gameSession.getQuiz().getTitle(),
                gameSession.getQuiz().getQuestions().size());
    }

    // private void showQuestionResults(GameSession gameSession) {
    // QuizQuestion currentQuestion = getCurrentQuestion(gameSession);
    // List<Map<String, Object>> playerResults = new ArrayList<>();

    // for (GamePlayer player : gameSession.getPlayers()) {
    // GameAnswer answer = findPlayerAnswer(player, currentQuestion.getId());
    // playerResults.add(Map.of(
    // "username", player.getUser().getUsername(),
    // "correct", answer != null ? answer.getIsCorrect() : false,
    // "points", answer != null ? answer.getPointsEarned() : 0,
    // "totalScore", player.getTotalScore()));
    // }

    // Map<String, Object> results = Map.of(
    // "type", "QUESTION_RESULTS",
    // "correctAnswer", currentQuestion.getQuestion().getCorrectAnswer(),
    // "playerResults", playerResults,
    // "leaderboard", getSimpleLeaderboard(gameSession));

    // messagingTemplate.convertAndSend("/topic/game/" + gameSession.getGameCode(),
    // results);
    // }

    // private void endGame(GameSession gameSession) {
    // gameSession.setStatus(GameStatus.FINISHED);
    // gameSession.setEndedAt(LocalDateTime.now());
    // gameSessionRepository.save(gameSession);

    // activeGames.remove(gameSession.getGameCode());
    // questionStartTimes.remove(gameSession.getId());

    // List<Map<String, Object>> finalLeaderboard =
    // getSimpleLeaderboard(gameSession);
    // String winner = finalLeaderboard.isEmpty() ? "No winner" : (String)
    // finalLeaderboard.get(0).get("username");

    // Map<String, Object> gameEnd = Map.of(
    // "type", "GAME_ENDED",
    // "winner", winner,
    // "finalLeaderboard", finalLeaderboard);

    // messagingTemplate.convertAndSend("/topic/game/" + gameSession.getGameCode(),
    // gameEnd);
    // }

    // private GameAnswer findPlayerAnswer(GamePlayer player, UUID questionId) {
    // for (GameAnswer answer : player.getAnswers()) {
    // if (answer.getQuizQuestion().getId().equals(questionId)) {
    // return answer;
    // }
    // }
    // return null;
    // }

    // private GameSessionDTO mapToDTO(GameSession gameSession) {
    // List<PlayerDTO> players = gameSession.getPlayers().stream()
    // .map(p -> new PlayerDTO(p.getUser().getUsername(), p.getTotalScore(), false))
    // .toList();

    // return new GameSessionDTO(
    // gameSession.getId(),
    // gameSession.getGameCode(),
    // gameSession.getStatus(),
    // gameSession.getQuiz().getTitle(),
    // gameSession.getCurrentQuestionIndex(),
    // gameSession.getQuiz().getQuestions().size(),
    // players,
    // gameSession.getCreatedAt());
    // }

}