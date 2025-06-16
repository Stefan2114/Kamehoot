import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Quiz } from "../types/quiz";
import QuizItem from "../components/QuizItem";
import { ApiService } from "../services/api";
import styles from "../styles/QuizzesPage.module.css";

const QuizzesPage = () => {
  const navigate = useNavigate();

  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("creationDate");
  const [orderDirection, setOrderDirection] = useState("desc");

  const fetchQuizzes = useCallback(async () => {
    try {
      const params = new URLSearchParams();

      if (searchTerm) {
        params.append("searchTerm", searchTerm);
      }

      params.append("orderBy", orderBy);
      params.append("orderDirection", orderDirection);

      const data = await ApiService.get<Quiz[]>(
        `/quizzes/private?${params.toString()}`
      );

      setQuizzes(data);
    } catch (error) {
      console.error("Error fetching quizzes:", error);
    }
  }, [searchTerm, orderBy, orderDirection]);

  useEffect(() => {
    fetchQuizzes();
  }, [fetchQuizzes]);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    setSearchTerm(e.target.value);

  const handleOrderByChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setOrderBy(e.target.value);
    if (orderBy === "creationDate") {
      setOrderDirection("desc");
    }
  };

  const handleAddQuiz = () => {
    navigate("/quizzes/add");
  };

  return (
    <div className={styles["quizzes-container"]}>
      <div className={styles["main-content"]}>
        <div className={styles["questions-action"]}>
          <button className={styles["add-button"]} onClick={handleAddQuiz}>
            Add Quiz
          </button>
          <div className={styles["search-container"]}>
            <input
              type="text"
              placeholder="Search quizzes..."
              value={searchTerm}
              onChange={handleSearchChange}
            />
          </div>
          <select
            className={styles["order-by-select"]}
            value={orderBy}
            onChange={handleOrderByChange}
          >
            <option value="creationDate">Sort by Date</option>
          </select>
        </div>

        <div className={styles["quizzes-list"]}>
          {quizzes.length > 0 ? (
            quizzes.map((quiz) => <QuizItem key={quiz.id} quiz={quiz} />)
          ) : (
            <div className={styles["no-quizzes"]}>No quizzes found</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default QuizzesPage;
