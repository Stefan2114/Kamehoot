// src/pages/QuestionPage.tsx

import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Question } from "../types/question";
import styles from "../styles/QuestionPage.module.css";
import { offlineService } from "../services/OfflineService";
import { useOffline } from "../contexts/OfflineContext";
import { ConnectionState } from "../services/OfflineService";

const QuestionPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { connectionState } = useOffline();
  const [question, setQuestion] = useState<Question | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchQuestion = async () => {
      if (!id) return;

      setLoading(true);
      try {
        const result = await offlineService.fetchQuestion(id);
        if (result) {
          setQuestion(result);
        } else {
          setError("Question not found");
        }
      } catch (err) {
        console.error("Error fetching question:", err);
        setError("Failed to load question");
      } finally {
        setLoading(false);
      }
    };

    fetchQuestion();
  }, [id]);

  const handleDeleteQuestion = async () => {
    if (!question) return;

    if (window.confirm("Are you sure you want to delete this question?")) {
      try {
        const success = await offlineService.deleteQuestion(question.id);
        if (success) {
          console.log("Question deleted successfully!");
          navigate("/questions");
        } else {
          throw new Error("Failed to delete question");
        }
      } catch (error) {
        console.error("Error deleting question:", error);
        alert("An error occurred while deleting the question.");
      }
    }
  };

  if (loading) {
    return <div className={styles["loading"]}>Loading question...</div>;
  }

  if (error || !question) {
    return (
      <div className={styles["error"]}>{error || "Question not found"}</div>
    );
  }

  const offlineMessage =
    connectionState !== ConnectionState.ONLINE ? (
      <div className={styles["offline-banner"]}>
        You are currently{" "}
        {connectionState === ConnectionState.OFFLINE
          ? "offline"
          : "unable to connect to the server"}
        . Changes will be synchronized when connection is restored.
      </div>
    ) : null;

  return (
    <div className={styles["question-detail-container"]}>
      {offlineMessage}
      <div className={styles["question-content"]}>
        <div className={styles["question-section"]}>
          <h3>Question:</h3>
          <label className={styles["question-text"]}>
            {question.questionText}
          </label>
        </div>
        <div className={styles["question-details"]}>
          <div className={styles["detail-section"]}>
            <label className={styles["detail-type"]}>Category:</label>
            <label className={styles["detail-text"]}>{question.category}</label>
          </div>
          <div className={styles["detail-section"]}>
            <label className={styles["detail-type"]}>Difficulty:</label>
            <label className={styles["detail-text"]}>
              {question.difficulty === 1
                ? "Easy"
                : question.difficulty === 2
                ? "Medium"
                : "Hard"}
            </label>
          </div>
        </div>
        <div className={styles["question-section"]}>
          <label className={styles["answer-type"]}>Correct answer:</label>
          <label className={styles["answer-text"]}>
            {question.correctAnswer}
          </label>
        </div>
        {question.wrongAnswers.map((wrongAnswer, index) => (
          <div key={index} className={styles["question-section"]}>
            <label className={styles["answer-type"]}>Wrong answer:</label>
            <label className={styles["answer-text"]}>{wrongAnswer}</label>
          </div>
        ))}
        <div className={styles["question-actions"]}>
          <Link
            to={`/questions/edit/${question.id}`}
            className={styles["edit-button"]}
          >
            Edit Question
          </Link>
          <button
            onClick={handleDeleteQuestion}
            className={styles["delete-button"]}
          >
            Delete Question
          </button>
          <Link to="/questions" className={styles["back-button"]}>
            Back to Questions
          </Link>
        </div>
      </div>
    </div>
  );
};

export default QuestionPage;
