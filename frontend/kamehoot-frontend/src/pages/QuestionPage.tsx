import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Question, QuestionFromBackend } from "../types/question";
import { ApiService } from "../utils/api";
import styles from "../styles/QuestionPage.module.css";

const QuestionPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [question, setQuestion] = useState<Question>();

  useEffect(() => {
    const fetchQuestion = async () => {
      try {
        if (id) {
          const data = await ApiService.get<QuestionFromBackend>(
            `/questions/${id}`
          );
          setQuestion({
            ...data,
            creationDate: new Date(data.creationDate.split(".")[0]),
          });
        }
      } catch (error) {
        console.error("Error fetching question:", error);
      }
    };

    fetchQuestion();
  }, [id]);

  if (!question) {
    return <div>Loading question...</div>;
  }

  const deleteQuestion = async (questionId: string) => {
    try {
      console.log("Trying to delete the question");
      await ApiService.delete(`/questions/${questionId}`);
      console.log("Question deleted successfully!");
      navigate("/questions");
    } catch (error) {
      console.error("Error deleting question:", error);
      alert("An error occurred while deleting the question.");
    }
  };

  const handleDeleteQuestion = () => {
    if (window.confirm("Are you sure you want to delete this question?")) {
      deleteQuestion(question.id);
    }
  };

  return (
    <div className={styles["question-detail-container"]}>
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
