import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Quiz } from "../types/quiz";
import { ApiService } from "../services/apiService";
import styles from "../styles/QuizPage.module.css";

const QuizPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState<Quiz>();

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        if (id) {
          const data = await ApiService.get<Quiz>(`/quizzes/${id}`);
          setQuiz(data);
        }
      } catch (error) {
        console.error("Error fetching quiz:", error);
      }
    };

    fetchQuiz();
  }, [id]);

  if (!quiz) {
    return <div>Loading quiz...</div>;
  }

  const deleteQuiz = async (quizId: string) => {
    try {
      console.log("Trying to delete the quiz");
      await ApiService.delete(`/quizzes/${quizId}`);
      console.log("quiz deleted successfully!");
      navigate("/quizzes");
    } catch (error) {
      console.error("Error deleting quiz:", error);
      alert("An error occurred while deleting the quiz.");
    }
  };

  const handleDeleteQuiz = () => {
    if (window.confirm("Are you sure you want to delete this quiz?")) {
      deleteQuiz(quiz.id);
    }
  };

  return (
    <div className={styles["quiz-detail-container"]}>
      <div className={styles["quiz-content"]}>
        <div className={styles["quiz-section"]}>
          <h2>Quiz:</h2>
          <label className={styles["quiz-text"]}>{quiz.title}</label>
        </div>
        <div className={styles["quiz-section"]}>
          <h3>Description:</h3>
          <label className={styles["quiz-text"]}>{quiz.description}</label>
        </div>
        <div className={styles["quiz-details"]}>
          <div className={styles["detail-section"]}>
            <label className={styles["detail-type"]}>Max Possible Score:</label>
            <label className={styles["detail-text"]}>
              {quiz.maxPossibleScore}
            </label>
          </div>
          <div className={styles["detail-section"]}>
            <label className={styles["detail-type"]}>Creation Date:</label>
            <label className={styles["detail-text"]}>{quiz.creationDate}</label>
          </div>
        </div>
        <div className={styles["quiz-section"]}>
          <h3>Questions:</h3>
          {quiz.questions.map((question, index) => (
            <div key={index} className={styles["question-section"]}>
              <h4>Question nr{index + 1}:</h4>
              <label className={styles["answer-text"]}>
                {question.questionText}
              </label>
              <label className={styles["answer-type"]}>Correct answer:</label>
              <label className={styles["answer-text"]}>
                {question.correctAnswer}
              </label>
            </div>
          ))}
        </div>

        <div className={styles["quiz-actions"]}>
          <button
            onClick={handleDeleteQuiz}
            className={styles["delete-button"]}
          >
            Delete quiz
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuizPage;
