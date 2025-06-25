import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Quiz } from "../types/quiz";
import { ApiService } from "../services/apiService";
import styles from "../styles/HomePage.module.css";
import PlayableQuizItem from "../components/PlayableQuizItem";

const HomePage = () => {
  const navigate = useNavigate();
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [gamePin, setGamePin] = useState("");

  const fetchQuizzes = useCallback(async () => {
    try {
      setIsLoading(true);
      console.log("Trying to fet quizzes");
      const params = new URLSearchParams();
      if (searchTerm) {
        params.append("searchTerm", searchTerm);
      }

      const data = await ApiService.get<Quiz[]>(
        `/quizzes?${params.toString()}`
      );

      setQuizzes(data);
    } catch (error) {
      console.error("Error fetching quizzes:", error);
    } finally {
      setIsLoading(false);
    }
  }, [searchTerm]);

  useEffect(() => {
    fetchQuizzes();
  }, [fetchQuizzes]);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    setSearchTerm(e.target.value);

  const handleCreateQuiz = () => {
    navigate("/quizzes/add");
  };

  const handleManageQuizzes = () => {
    navigate("/quizzes");
  };

  const getWelcomeMessage = () => {
    const hour = new Date().getHours();
    if (hour < 12) return "Good morning!";
    if (hour < 17) return "Good afternoon!";
    return "Good evening!";
  };

  return (
    <div className={styles["home-container"]}>
      <div className={styles["welcome-section"]}>
        <div className={styles["welcome-content"]}>
          <h1 className={styles["welcome-title"]}>
            {getWelcomeMessage()} Ready to test your knowledge?
          </h1>
          <p className={styles["welcome-subtitle"]}>
            Choose from our collection of engaging quizzes and challenge
            yourself!
          </p>
          <div className={styles["welcome-actions"]}>
            <button
              className={styles["primary-button"]}
              onClick={handleCreateQuiz}
            >
              Create New Quiz
            </button>
            <button
              className={styles["secondary-button"]}
              onClick={handleManageQuizzes}
            >
              Manage My Quizzes
            </button>
          </div>
        </div>
      </div>

      <div className={styles["main-content"]}>
        <div className={styles["content-header"]}>
          <h2 className={styles["section-title"]}>Available Quizzes</h2>
          <div className={styles["quiz-actions"]}>
            <div className={styles["search-container"]}>
              <input
                type="text"
                placeholder="Search quizzes..."
                value={searchTerm}
                onChange={handleSearchChange}
                className={styles["search-input"]}
              />
            </div>
          </div>
        </div>

        <div className={styles["quiz-actions"]}>
          <div className={styles["search-container"]}>
            <input
              type="text"
              placeholder="Search quizzes..."
              value={searchTerm}
              onChange={handleSearchChange}
              className={styles["search-input"]}
            />
          </div>

          <div className={styles["game-actions"]}>
            <button
              className={styles["create-game-button"]}
              onClick={() => navigate("/games/create")}
            >
              🎮 Create Game
            </button>

            <div className={styles["join-game-container"]}>
              <input
                type="text"
                placeholder="Enter Game PIN"
                value={gamePin}
                onChange={(e) => setGamePin(e.target.value)}
                className={styles["join-game-input"]}
              />
              <button
                className={styles["join-game-button"]}
                onClick={() => {
                  if (gamePin.trim()) navigate(`/game/${gamePin}/lobby`);
                }}
              >
                🔗 Join Game
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
