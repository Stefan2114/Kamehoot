import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Question } from "../types/question";
import "../styles/QuestionPage.css";

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
    <div className="question-detail-container">
      <div className="question-content">
        <div className="question-section">
          <h3>Question:</h3>
          <label className="question-text">{question.questionText}</label>
        </div>
        <div className="question-details">
          <div className="detail-section">
            <label className="detail-type">Category:</label>
            <label className="detail-text">{question.category}</label>
          </div>
          <div className="detail-section">
            <label className="detail-type">Difficulty:</label>
            <label className="detail-text">{question.difficulty}</label>
          </div>
        </div>
        <div className="question-section">
          <label className="answer-type">Correct answer:</label>
          <label className="answer-text">{question.correctAnswer}</label>
        </div>

        {question.wrongAnswers.map((wrongAnswer) => (
          <div className="question-section">
            <label className="answer-type">Wrong answer:</label>
            <label className="answer-text">{wrongAnswer}</label>
          </div>
        ))}

        <div className="question-actions">
          <Link to={`/questions/edit/${question.id}`} className="edit-button">
            Edit Question
          </Link>
          <button onClick={handleDeleteQuestion} className="delete-button">
            Delete Question
          </button>
          <Link to="/questions" className="back-button">
            Back to Questions
          </Link>
        </div>
      </div>
    </div>
  );
};

export default QuestionPage;
