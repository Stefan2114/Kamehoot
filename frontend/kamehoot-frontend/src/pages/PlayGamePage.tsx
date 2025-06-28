import React, { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  GameQuestionDTO,
  GameSessionDTO,
  PlayerDTO,
  WebSocketDTO,
} from "../types/game";
import { GameService } from "../services/gameService";
import styles from "../styles/PlayGamePage.module.css";

const PlayGamePage: React.FC = () => {
  const navigate = useNavigate();
  const { gameCode } = useParams<{ gameCode: string }>();
  const MAX_PLAYERS_TO_SHOW = 5;

  // State variables
  const [players, setPlayers] = useState<PlayerDTO[]>([]);
  const [gameSession, setGameSession] = useState<GameSessionDTO | null>(null);
  const [gameQuestion, setGameQuestion] = useState<GameQuestionDTO | null>(
    null
  );
  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [playersAnswered, setPlayersAnswered] = useState(0);
  const [isHost, setIsHost] = useState(false);
  const [selectedAnswer, setSelectedAnswer] = useState<string>("");
  const [hasAnswered, setHasAnswered] = useState(false);
  const [timeLeft, setTimeLeft] = useState<number>(0);
  const [showLeaderboard, setShowLeaderboard] = useState(false);

  // Loading states to prevent race conditions
  const [gameSessionLoaded, setGameSessionLoaded] = useState(false);
  const [hostCheckComplete, setHostCheckComplete] = useState(false);
  const [socketConnected, setSocketConnected] = useState(false);
  const [gameJoined, setGameJoined] = useState(false);

  const fetchGameSession = async () => {
    if (!gameCode) {
      navigate("/games");
      return;
    }

    try {
      console.log("Fetching game session for code:", gameCode);
      const data = await GameService.getGameSession(gameCode);
      console.log("Game session fetched:", data);
      setGameSession(data);
      if (gameSessionLoaded === false) {
        setGameSessionLoaded(true);
      }
    } catch (error) {
      console.error("Error fetching gameSession:", error);
      navigate("/games");
    }
  };
  // Fetch game session - runs once when component mounts
  useEffect(() => {
    fetchGameSession();
  }, [gameCode]);

  // Check if user is host - runs once when gameSession is loaded
  useEffect(() => {
    const fetchIsHost = async () => {
      if (!gameSession || !gameSessionLoaded) return;

      try {
        console.log("Checking if user is host for game:", gameSession.id);
        const hostStatus = await GameService.isHost(gameSession.id);
        console.log("Host status:", hostStatus);
        setIsHost(hostStatus);
        setHostCheckComplete(true);
      } catch (error) {
        console.error("Error fetching isHost:", error);
        setHostCheckComplete(true); // Set to true even on error to continue flow
      }
    };

    fetchIsHost();
  }, [gameSessionLoaded]);

  // Connect to WebSocket - runs when game session and host check are complete
  useEffect(() => {
    if (!gameSession || !gameSessionLoaded) return;

    console.log("Connecting to WebSocket for game:", gameSession.id);

    const webSocket = new WebSocket(
      `wss://localhost:8443/game?gameSessionId=${gameSession.id}`
    );

    webSocket.onopen = () => {
      console.log("Connected to WebSocket");
      setSocketConnected(true);
    };

    webSocket.onmessage = (event) => {
      const data: WebSocketDTO = JSON.parse(event.data);

      if (data.type === "leaderboard" || data.type === "playersJoined") {
        if (Array.isArray(data.info)) {
          const newPlayers: PlayerDTO[] = data.info;
          setPlayers(newPlayers);
          setPlayersAnswered(
            newPlayers.filter((player) => player.hasAnswered).length
          );

          if (data.type === "leaderboard") {
            fetchGameSession();

            setShowLeaderboard(true);
            setHasAnswered(false);
            setSelectedAnswer("");
          }
        }
      } else if (data.type === "gameQuestion") {
        const newGameQuestion: GameQuestionDTO = data.info as GameQuestionDTO;
        setGameQuestion(newGameQuestion);
        setShowLeaderboard(false);
        setHasAnswered(false);
        setSelectedAnswer("");
        setTimeLeft(newGameQuestion.timeLimit);
      }
    };

    webSocket.onclose = () => {
      console.log("WebSocket connection closed");
      setSocketConnected(false);
    };

    webSocket.onerror = (error) => {
      console.error("WebSocket error:", error);
      setSocketConnected(false);
    };

    setSocket(webSocket);

    // Cleanup function
    return () => {
      console.log("Closing WebSocket connection");
      webSocket.close();
      setSocketConnected(false);
    };
  }, [gameSessionLoaded]);

  // Join game if not host - runs after socket is connected and user is not host
  useEffect(() => {
    const joinGame = async () => {
      if (
        !gameSession ||
        !socketConnected ||
        !hostCheckComplete ||
        isHost ||
        gameJoined
      )
        return;

      try {
        console.log("Attempting to join game:", gameSession.id);
        await GameService.joinGame(gameSession.id);
        console.log("Successfully joined game");
        setGameJoined(true);
      } catch (error) {
        console.error("Error joining game:", error);
        // You might want to show an error message to the user here
      }
    };

    joinGame();
  }, [hostCheckComplete, socketConnected]);

  // Timer for questions
  useEffect(() => {
    if (gameQuestion && timeLeft > 0 && !hasAnswered) {
      const timer = setTimeout(() => {
        setTimeLeft(timeLeft - 1);
      }, 1000);
      return () => clearTimeout(timer);
    }
  }, [timeLeft, hasAnswered, gameQuestion]);

  // Event handlers (using useCallback to prevent unnecessary re-renders)
  const handleStartGame = useCallback(async () => {
    if (!gameSession || !isHost) return;

    try {
      await GameService.startGame(gameSession.id);
    } catch (error) {
      console.error("Error starting game:", error);
    }
  }, [gameSession, isHost]);

  const handleAnswerSelect = useCallback(
    async (answer: string) => {
      console.log("I want to answer");
      if (hasAnswered || !gameQuestion || !gameSession || isHost) return;

      setSelectedAnswer(answer);
      setHasAnswered(true);

      try {
        await GameService.submitAnswer({
          gameSessionId: gameSession.id,
          questionId: gameQuestion.questionId,
          answer: answer,
          answerTime: new Date().toISOString(),
        });
      } catch (error) {
        console.error("Error submitting answer:", error);
      }
    },
    [hasAnswered, gameQuestion, gameSession]
  );

  const handleNextQuestion = useCallback(async () => {
    if (!gameSession || !isHost) return;

    try {
      await GameService.nextQuestion(gameSession.id);
    } catch (error) {
      console.error("Error moving to next question:", error);
      // If error suggests game is over, handle end game
    }
  }, [gameSession, isHost]);

  const handleBackToHome = useCallback(() => {
    navigate("/home");
  }, [navigate]);

  // Loading state
  if (
    !gameSession ||
    !gameSessionLoaded ||
    !hostCheckComplete ||
    !socketConnected
  ) {
    return <div className={styles["loading-container"]}>Loading game...</div>;
  }

  // Waiting lobby
  if (gameSession.status === "WAITING") {
    return (
      <div className={styles["game-container"]}>
        <div className={styles["game-content"]}>
          <div className={styles["game-header"]}>
            <h1>Game Lobby: {gameSession.quizTitle}</h1>
            <h2>Players ({players.length})</h2>
          </div>

          <div className={styles["players-section"]}>
            {players.map((player, index) => (
              <div key={index} className={styles["player-item"]}>
                {player.username}
              </div>
            ))}
          </div>

          {isHost && (
            <button
              onClick={handleStartGame}
              className={styles["start-button"]}
            >
              Start Game
            </button>
          )}

          {!isHost && (
            <p className={styles["waiting-text"]}>
              Waiting for host to start the game...
            </p>
          )}
        </div>
      </div>
    );
  }

  // Game ended
  if (gameSession.status === "FINISHED") {
    return (
      <div className={styles["game-container"]}>
        <div className={styles["game-content"]}>
          <div className={styles["finished-container"]}>
            <div className={styles["finished-header"]}>
              <h1>🎉 Game Finished!</h1>
              <h2>Final Leaderboard</h2>
            </div>

            <div className={styles["players-section"]}>
              {players.slice(0, MAX_PLAYERS_TO_SHOW).map((player, index) => (
                <div
                  key={index}
                  className={`${styles["leaderboard-item"]} ${
                    index === 0 ? styles["winner-item"] : ""
                  }`}
                >
                  <span className={styles["leaderboard-name"]}>
                    #{index + 1} {player.username}
                  </span>
                  <span className={styles["leaderboard-score"]}>
                    {player.totalScore} points
                  </span>
                </div>
              ))}
            </div>

            <button
              onClick={handleBackToHome}
              className={styles["home-button"]}
            >
              Back to Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Show leaderboard between questions
  if (showLeaderboard) {
    return (
      <div className={styles["game-container"]}>
        <div className={styles["game-content"]}>
          <div className={styles["game-header"]}>
            <h1>New {gameQuestion?.questionNumber} Leaderboard</h1>
          </div>

          <div className={styles["players-section"]}>
            {players.slice(0, MAX_PLAYERS_TO_SHOW).map((player, index) => (
              <div key={index} className={styles["leaderboard-item"]}>
                <span className={styles["leaderboard-name"]}>
                  #{index + 1} {player.username}
                </span>
                <span className={styles["leaderboard-score"]}>
                  {player.totalScore} points
                </span>
              </div>
            ))}
          </div>

          {isHost && (
            <button
              onClick={handleNextQuestion}
              className={styles["next-button"]}
            >
              Next Question
            </button>
          )}

          {!isHost && (
            <p className={styles["waiting-text"]}>
              Waiting for host to continue...
            </p>
          )}
        </div>
      </div>
    );
  }

  // Active question
  if (gameQuestion) {
    return (
      <div className={styles["game-container"]}>
        <div className={styles["game-content"]}>
          <div className={styles["question-header"]}>
            <h2 className={styles["question-title"]}>
              Question {gameQuestion.questionNumber + 1}
            </h2>
            <div className={styles["question-info"]}>
              <div
                className={`${styles["timer"]} ${
                  timeLeft <= 5
                    ? styles["timer-urgent"]
                    : styles["timer-normal"]
                }`}
              >
                Time: {timeLeft}s
              </div>
              <div className={styles["answered-count"]}>
                Answered: {playersAnswered}/{players.length}
              </div>
            </div>
          </div>

          <div className={styles["question-content"]}>
            <h3 className={styles["question-text"]}>
              {gameQuestion.questionText}
            </h3>

            <div className={styles["options-grid"]}>
              {gameQuestion.options.map((option, index) => (
                <button
                  key={index}
                  onClick={() => handleAnswerSelect(option)}
                  disabled={hasAnswered || isHost}
                  className={`${styles["option-button"]} ${
                    selectedAnswer === option ? styles["option-selected"] : ""
                  } ${
                    hasAnswered && selectedAnswer !== option
                      ? styles["option-faded"]
                      : ""
                  }`}
                >
                  {option}
                </button>
              ))}
            </div>

            {hasAnswered && (
              <p className={styles["answer-submitted"]}>
                Answer submitted! Waiting for other players...
              </p>
            )}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={styles["loading-container"]}>
      <p>Loading question...</p>
    </div>
  );
};

export default PlayGamePage;
