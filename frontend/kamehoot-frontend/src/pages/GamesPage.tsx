// components/GamesPage.tsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { GameService } from "../services/gameService";
import styles from "../styles/GamesPage.module.css";

const GamesPage: React.FC = () => {
  const navigate = useNavigate();
  const [gameCode, setGameCode] = useState("");
  const [isJoining, setIsJoining] = useState(false);
  const [joinError, setJoinError] = useState("");

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Game Center</h1>

      <div className={styles["actions-grid"]}>
        <button
          onClick={() => {
            navigate("/games/create");
          }}
          className={`${styles["action-button"]} ${styles["create-game"]}`}
        >
          <div className={styles.icon}>🎮</div>
          <h2>Create Game</h2>
          <p>Start a new quiz game with your friends</p>
        </button>

        <div className={`${styles["action-button"]} ${styles["join-game"]}`}>
          <div className={styles.icon}>🚪</div>
          <h2>Join Game</h2>
          <p>Enter a game code to join an existing game</p>
          <input
            type="text"
            placeholder="Enter game code"
            value={gameCode}
            onChange={(event) => {
              setGameCode(event.target.value);
              setJoinError(""); // Clear error when typing
            }}
            className={styles["game-code-input"]}
            disabled={isJoining}
          />
          <button
            onClick={() => {
              navigate(`/games/${gameCode}`);
            }}
            disabled={isJoining || !gameCode.trim()}
            className={styles["join-button"]}
          >
            {isJoining ? "Joining..." : "Join Game"}
          </button>
          {joinError && <p className={styles["error-message"]}>{joinError}</p>}
        </div>
      </div>

      <div className={styles["how-to-play"]}>
        <h2>How to Play</h2>
        <div className={styles.steps}>
          <div className={styles.step}>
            <h3>1. Create or Join</h3>
            <p>Create a new game with your quiz or join with a game code</p>
          </div>
          <div className={styles.step}>
            <h3>2. Wait in Lobby</h3>
            <p>Players join the lobby and wait for the host to start</p>
          </div>
          <div className={styles.step}>
            <h3>3. Answer Questions</h3>
            <p>Answer multiple choice questions as fast as you can</p>
          </div>
          <div className={styles.step}>
            <h3>4. See Results</h3>
            <p>View results after each question and final leaderboard</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GamesPage;
