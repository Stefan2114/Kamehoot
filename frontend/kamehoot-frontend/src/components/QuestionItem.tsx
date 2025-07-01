import React from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import styles from "../styles/QuestionItem.module.css";

interface QuestionItemProps {
  question: Question;
}

const QuestionItem: React.FC<QuestionItemProps> = ({ question }) => {
  const navigate = useNavigate();

  const handleSeeMoreClick = () => {
    navigate(`/questions/${question.id}`);
  };

  return (
    <div className={styles["question-item"]}>
      <div className={styles["question-content"]}>
        <div className={styles["question-text"]}>{question.questionText}</div>
        <div className={styles["question-footer"]}>
          <div className={styles["question-details"]}>
            <span className={styles["detail-item"]}>
              <span className={styles["detail-label"]}>Category:</span>
              <span className={styles["detail-value"]}>
                {question.category}
              </span>
            </span>
            <span className={styles["detail-item"]}>
              <span className={styles["detail-label"]}>Difficulty:</span>
              <span className={styles["detail-value"]}>
                {question.difficulty === 1
                  ? "Easy"
                  : question.difficulty === 2
                  ? "Medium"
                  : "Hard"}
              </span>
            </span>
            <span className={styles["detail-item"]}>
              <span className={styles["detail-label"]}>Date:</span>
              <span className={styles["detail-value"]}>
                {question.creationDate}
              </span>
            </span>
          </div>
          <button
            className={styles["see-more-btn"]}
            onClick={handleSeeMoreClick}
          >
            See more info
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuestionItem;
