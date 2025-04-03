import React from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import "../styles/QuestionItem.css";

interface QuestionItemProps {
  question: Question;
}

const QuestionItem: React.FC<QuestionItemProps> = ({ question }) => {
  const navigate = useNavigate();

  const handleSeeMoreClick = () => {
    navigate(`/questions/${question.id}`);
  };

  return (
    <div className="question-item">
      <div className="question-content">
        <div className="question-text">{question.questionText}</div>
        <div className="question-footer">
          <div className="question-details">
            <span className="detail-item">
              <span className="detail-label">Category:</span>
              <span className="detail-value">{question.category}</span>
            </span>
            <span className="detail-item">
              <span className="detail-label">Difficulty:</span>
              <span className="detail-value">{question.difficulty}</span>
            </span>
          </div>
          <button className="see-more-btn" onClick={handleSeeMoreClick}>
            See more info
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuestionItem;
