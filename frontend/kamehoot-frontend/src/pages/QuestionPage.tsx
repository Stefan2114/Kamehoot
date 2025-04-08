import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Question } from "../types/question";
import styles from "../styles/QuestionPage.module.css";

const QuestionPage = () => {
  const { id } = useParams<{ id: string }>();

  const navigate = useNavigate();

  const [question, setQuestion] = useState<Question>();

  useEffect(() => {
    fetch(`http://localhost:8081/questions/${id}`)
      .then((response) => response.json())
      .then((data: Question) => setQuestion(data))
      .catch((error) => console.error("Error fetching question:", error));
  });

  if (!question) {
    return <div>Couldn't fetch the question</div>;
  }

  const deleteQuestion = async (questionId: number) => {
    try {
      const response = await fetch(
        `http://localhost:8081/questions/${questionId}`,
        {
          method: "DELETE",
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete question");
      }

      console.log("Question deleted successfully!");
      navigate("/questions");
    } catch (error) {
      console.error("Error deleting question:", error);
      alert("An error occurred while deleting the question.");
    }
  };

  const handleDeleteQuestion = () => {
    deleteQuestion(question.id);
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
              {question.difficulty}
            </label>
          </div>
        </div>
        <div className={styles["question-section"]}>
          <label className={styles["answer-type"]}>Correct answer:</label>
          <label className={styles["answer-text"]}>
            {question.correctAnswer}
          </label>
        </div>

        {question.wrongAnswers.map((wrongAnswer) => (
          <div className={styles["question-section"]}>
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
