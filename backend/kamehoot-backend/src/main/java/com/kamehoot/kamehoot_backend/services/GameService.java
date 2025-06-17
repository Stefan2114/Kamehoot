package com.kamehoot.kamehoot_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.DTOs.CreateGameRequest;
import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;
import com.kamehoot.kamehoot_backend.DTOs.PlayerAnswerDTO;
import com.kamehoot.kamehoot_backend.DTOs.PlayerDTO;
import com.kamehoot.kamehoot_backend.DTOs.QuestionResultDTO;
import com.kamehoot.kamehoot_backend.DTOs.SubmitAnswerRequest;
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

    private final Map<String, GameSession> activeGames = new ConcurrentHashMap<>();

    public GameService(IGameSessionRepository gameSessionRepository, IGamePlayerRepository gamePlayerRepository,
            IGameAnswerRepository gameAnswerRepository, IQuizRepository quizRepository,
            IUserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.gameAnswerRepository = gameAnswerRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GameSessionDTO createGame(String hostUsername, CreateGameRequest request) {
        AppUser host = this.userRepository.findByUsername(hostUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Quiz quiz = this.quizRepository.findById(request.quizId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GameSession gameSession = new GameSession();
        gameSession.setQuiz(quiz);
        gameSession.setHost(host);

        gameSession.setGameCode(generateGameCode());
        gameSession.setCreatedAt(LocalDateTime.now());
        gameSession.setQuestionTimeLimit(request.questionTimeLimit());

        try {
            GameSession savedGameSession = this.gameSessionRepository.save(gameSession);
            activeGames.put(savedGameSession.getGameCode(), savedGameSession);
            return mapToDTO(savedGameSession);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't create game session");
        }

    }

    @Override
    public GameSessionDTO joinGame(String username, String gameCode) {

        AppUser user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GameSession gameSession = activeGames.get(gameCode);
        if (gameSession == null) {
            gameSession = gameSessionRepository.findByGameCode(gameCode)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        }

        if (gameSession.getStatus() != GameStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game already started");
        }

        boolean alreadyJoined = gameSession.getPlayers().stream()
                .anyMatch(player -> player.getUser().getUsername().equals(username));

        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already joined the game");
        }

        GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setGameSession(gameSession);
        gamePlayer.setUser(user);

        gamePlayer.setJoinedAt(LocalDateTime.now());

        try {

            gamePlayerRepository.save(gamePlayer);
            gameSession.getPlayers().add(gamePlayer);
            return mapToDTO(gameSession);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong joining the session");
        }

    }

    @Override
    public void startGame(String hostUsername, UUID gameSessionId) {

        GameSession gameSession = this.gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!gameSession.getHost().getUsername().equals(hostUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host can start the game");
        }

        if (gameSession.getPlayers().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Need at least 1 player to start the game");
        }

        gameSession.setStatus(GameStatus.IN_PROGRESS);
        gameSession.setStartedAt(LocalDateTime.now());
        gameSession.setCurrentQuestionIndex(0);

        try {
            this.gameSessionRepository.save(gameSession);
            this.activeGames.put(gameSession.getGameCode(), gameSession);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Something went wrong while updating the gameSession");
        }

    }

    @Override
    public void submitAnswer(String username, SubmitAnswerRequest request) {

        GameSession gameSession = this.gameSessionRepository.findById(request.gameSessionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (gameSession.getStatus() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is not active");

        }

        GamePlayer gamePlayer = gameSession.getPlayers().stream()
                .filter(player -> player.getUser().getUsername().equals(username)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not in game"));
        QuizQuestion currentQuestion = getCurrentQuestion(gameSession);
        if (!currentQuestion.getQuestion().getId().equals(request.questionId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid question");
        }

        boolean alreadyAnswered = gamePlayer.getAnswers().stream()
                .anyMatch(answer -> answer.getQuizQuestion().getId().equals(currentQuestion.getId()));

        if (alreadyAnswered) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question already answered");
        }

        GameAnswer gameAnswer = new GameAnswer();
        gameAnswer.setAnsweredAt(LocalDateTime.now());
        gameAnswer.setGamePlayer(gamePlayer);
        gameAnswer.setQuizQuestion(currentQuestion);
        gameAnswer.setUserAnswer(request.answer());

        long responseTime = calculateResponseTime(gameSession);
        gameAnswer.setResponseTime(responseTime);

        boolean isCorrect = currentQuestion.getQuestion().getCorrectAnswer().equals(request.answer());
        gameAnswer.setIsCorrect(isCorrect);

        if (isCorrect) {
            int points = calculatePoints(responseTime, gameSession.getQuestionTimeLimit());
            gameAnswer.setPointsEarned(points);
            gamePlayer.setTotalScore(gamePlayer.getTotalScore() + points);
        }

        try {
            this.gameAnswerRepository.save(gameAnswer);
            this.gamePlayerRepository.save(gamePlayer);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong saving the answer");
        }

    }

    @Override
    public QuestionResultDTO getQuestionResults(UUID gameSessionId) {

        GameSession gameSession = this.gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found"));

        QuizQuestion currentQuestion = getCurrentQuestion(gameSession);

        List<PlayerAnswerDTO> playerAnswers = gameSession.getPlayers().stream().map(player -> {
            GameAnswer answer = player.getAnswers().stream()
                    .filter((playerAnswer -> playerAnswer.getQuizQuestion().getId().equals(currentQuestion.getId())))
                    .findFirst()
                    .orElse(null);

            if (answer != null) {
                return new PlayerAnswerDTO(player.getUser().getUsername(), answer.getUserAnswer(),
                        answer.getIsCorrect(), answer.getResponseTime(), answer.getPointsEarned());
            }
            return null;
        })
                .filter(Objects::nonNull)
                .toList();

        List<PlayerDTO> leaderboard = getLeaderBoard(gameSession);

        return new QuestionResultDTO(
                currentQuestion.getQuestion().getId(),
                currentQuestion.getQuestion().getQuestionText(),
                currentQuestion.getQuestion().getCorrectAnswer(),
                playerAnswers,
                leaderboard);
    }

    @Override
    public void nextQuestion(String hostUsername, UUID gameSessionId) {
        GameSession gameSession = this.gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        if (!gameSession.getHost().getUsername().equals(hostUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host can start the game");

        }

        if (gameSession.getCurrentQuestionIndex() + 1 >= gameSession.getQuiz().getQuestions().size()) {
            gameSession.setStatus(GameStatus.FINISHED);
            gameSession.setEndedAt(LocalDateTime.now());
        } else {
            gameSession.setCurrentQuestionIndex(gameSession.getCurrentQuestionIndex() + 1);
        }

        try {
            this.gameSessionRepository.save(gameSession);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error saving the game session");
        }

        if (gameSession.getStatus() == GameStatus.FINISHED) {
            activeGames.remove(gameSession.getGameCode());

        }

    }

    private String generateGameCode() {
        String code;
        do {
            code = String.format("%06d", new Random().nextInt(1000000));
        } while (gameSessionRepository.existsByGameCode(code));
        return code;
    }

    private long calculateResponseTime(GameSession gameSession) {

        // This is simplified - in real implementation, you'd track when question was
        // shown
        return System.currentTimeMillis() % 30000; // Mock response time
    }

    @Override
    public int calculatePoints(long responseTime, int timeLimit) {
        double timeFactor = 1.0 - (responseTime / (timeLimit * 1000.0));
        return Math.max(1, (int) (1000 * timeFactor));
    }

    private QuizQuestion getCurrentQuestion(GameSession gameSession) {
        List<QuizQuestion> questions = gameSession.getQuiz().getQuestions();
        if (gameSession.getCurrentQuestionIndex() >= questions.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No more questions");
        }

        return questions.get(gameSession.getCurrentQuestionIndex());
    }

    private boolean hasAnsweredCurrentQuestion(GamePlayer player, GameSession gameSession) {
        QuizQuestion currentQuestion = getCurrentQuestion(gameSession);
        return player.getAnswers().stream()
                .anyMatch(answer -> answer.getQuizQuestion().getId().equals(currentQuestion.getId()));
    }

    private List<PlayerDTO> getLeaderBoard(GameSession gameSession) {
        return gameSession.getPlayers().stream()
                .sorted((player1, player2) -> player1.getTotalScore().compareTo(player2.getTotalScore()))
                .map(player -> new PlayerDTO(player.getUser().getUsername(), player.getTotalScore(),
                        hasAnsweredCurrentQuestion(player, gameSession)))
                .toList();
    }

    private GameSessionDTO mapToDTO(GameSession gameSession) {
        return new GameSessionDTO(gameSession.getId(), gameSession.getGameCode(), gameSession.getStatus(),
                gameSession.getQuiz().getTitle(), gameSession.getCurrentQuestionIndex(),
                gameSession.getQuiz().getQuestions().size(), getLeaderBoard(gameSession), gameSession.getCreatedAt());
    }

}
