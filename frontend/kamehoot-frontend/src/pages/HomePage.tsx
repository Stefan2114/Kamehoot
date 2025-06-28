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
      console.log("Trying to fetch quizzes");
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

  const handleNavigateToGames = () => {
    navigate("/games");
  };

  const handleNavigateToQuizzes = () => {
    navigate("/quizzes");
  };

  const handleNavigateToQuestions = () => {
    navigate("/questions");
  };

  const handleCreateQuiz = () => {
    navigate("/quizzes/add");
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
            Explore our platform - manage your quizzes, create engaging games,
            and build custom questions!
          </p>
        </div>
      </div>

      <div className={styles["main-content"]}>
        <div className={styles["navigation-section"]}>
          <h2 className={styles["section-title"]}>
            What would you like to do?
          </h2>
          <div className={styles["navigation-cards"]}>
            <div className={styles["nav-card"]}>
              <div className={styles["nav-card-icon"]}>🎮</div>
              <h3 className={styles["nav-card-title"]}>Games</h3>
              <p className={styles["nav-card-description"]}>
                Create interactive game sessions, manage game rooms, and host
                live quiz competitions
              </p>
              <button
                className={styles["nav-card-button"]}
                onClick={handleNavigateToGames}
              >
                Go to Games
              </button>
            </div>

            <div className={styles["nav-card"]}>
              <div className={styles["nav-card-icon"]}>📚</div>
              <h3 className={styles["nav-card-title"]}>Quizzes</h3>
              <p className={styles["nav-card-description"]}>
                Browse, create, and manage your quiz collections with
                comprehensive question sets
              </p>
              <button
                className={styles["nav-card-button"]}
                onClick={handleNavigateToQuizzes}
              >
                Go to Quizzes
              </button>
            </div>

            <div className={styles["nav-card"]}>
              <div className={styles["nav-card-icon"]}>❓</div>
              <h3 className={styles["nav-card-title"]}>Questions</h3>
              <p className={styles["nav-card-description"]}>
                Create, edit, and organize individual questions for your quiz
                content
              </p>
              <button
                className={styles["nav-card-button"]}
                onClick={handleNavigateToQuestions}
              >
                Go to Questions
              </button>
            </div>
          </div>
        </div>

        <div className={styles["quick-actions-section"]}>
          <h2 className={styles["section-title"]}>Quick Actions</h2>
          <div className={styles["quick-actions"]}>
            <button
              className={styles["quick-action-button"]}
              onClick={handleCreateQuiz}
            >
              ➕ Create New Quiz
            </button>

            <button
              className={styles["quick-action-button"]}
              onClick={() => navigate("/games/create")}
            >
              🎯 Start New Game
            </button>

            <div className={styles["join-game-section"]}>
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
                disabled={!gamePin.trim()}
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
