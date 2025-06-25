import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { GameService } from "../services/gameService";
import { ApiService } from "../services/apiService";
import { Quiz } from "../types/quiz";
import styles from "../styles/CreateGamePage.module.css";

const CreateGamePage: React.FC = () => {
  const navigate = useNavigate();
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [selectedQuizId, setSelectedQuizId] = useState<string>("");
  const [timeLimit, setTimeLimit] = useState<number>(15);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchQuizzes = async () => {
      try {
        const data = await ApiService.get<Quiz[]>("/quizzes");
        setQuizzes(data);
      } catch (error) {
        console.error("Error fetching quizzes:", error);
      }
    };
    fetchQuizzes();
  }, []);

  const handleCreateGame = async () => {
    if (!selectedQuizId) {
      alert("Please select a quiz");
      return;
    }

    setLoading(true);
    try {
      const gameSession = await GameService.createGame({
        quizId: selectedQuizId,
        questionTimeLimit: timeLimit,
      });
      navigate(`/game/lobby/${gameSession.gameCode}`);
    } catch (error) {
      console.error("Error creating game:", error);
      alert("Failed to create game");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles["create-game-container"]}>
      <div className={styles["create-game-content"]}>
        <h1>Create New Game</h1>

        <div className={styles["form-section"]}>
          <label>Select Quiz:</label>
          <select
            value={selectedQuizId}
            onChange={(e) => setSelectedQuizId(e.target.value)}
          >
            <option value="">Choose a quiz...</option>
            {quizzes.map((quiz) => (
              <option key={quiz.id} value={quiz.id}>
                {quiz.title} ({quiz.questions.length} questions)
              </option>
            ))}
          </select>
        </div>

        <div className={styles["form-section"]}>
          <label>Time Limit per Question (seconds):</label>
          <input
            type="number"
            min="5"
            max="120"
            value={timeLimit}
            onChange={(e) => setTimeLimit(Number(e.target.value))}
          />
        </div>

        <div className={styles["form-actions"]}>
          <button
            onClick={handleCreateGame}
            disabled={loading || !selectedQuizId}
          >
            {loading ? "Creating..." : "Create Game"}
          </button>
          <button onClick={() => navigate("/games")}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default CreateGamePage;
