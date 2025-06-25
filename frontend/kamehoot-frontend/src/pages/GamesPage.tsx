// components/GamesPage.tsx
import React from "react";
import { Link } from "react-router-dom";
import styles from "../styles/GamesPage.module.css";

const GamesPage: React.FC = () => {
  return (
    <div className={styles["games-page-container"]}>
      <div className={styles["games-content"]}>
        <h1>Game Center</h1>

        <div className={styles["game-actions"]}>
          <Link
            to="/games/create"
            className={styles["action-button create-game"]}
          >
            <div className={styles["button-icon"]}>🎮</div>
            <div className={styles["button-content"]}>
              <h3>Create Game</h3>
              <p>Start a new quiz game with your friends</p>
            </div>
          </Link>

          <Link to="/games/join" className={styles["action-button join-game"]}>
            <div className={styles["button-icon"]}>🚪</div>
            <div className={styles["button-content"]}>
              <h3>Join Game</h3>
              <p>Enter a game code to join an existing game</p>
            </div>
          </Link>
        </div>

        <div className={styles["features"]}>
          <h2>How to Play</h2>
          <div className={styles["feature-list"]}>
            <div className={styles["feature-item"]}>
              <h4>1. Create or Join</h4>
              <p>Create a new game with your quiz or join with a game code</p>
            </div>
            <div className={styles["feature-item"]}>
              <h4>2. Wait in Lobby</h4>
              <p>Players join the lobby and wait for the host to start</p>
            </div>
            <div className={styles["feature-item"]}>
              <h4>3. Answer Questions</h4>
              <p>Answer multiple choice questions as fast as you can</p>
            </div>
            <div className={styles["feature-item"]}>
              <h4>4. See Results</h4>
              <p>View results after each question and final leaderboard</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GamesPage;
