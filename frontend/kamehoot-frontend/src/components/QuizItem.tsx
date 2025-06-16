import { useNavigate } from "react-router-dom";
import { Quiz } from "../types/quiz";
import styles from "../styles/QuizItem.module.css";

interface QuizItemProps {
  quiz: Quiz;
}

const QuizItem: React.FC<QuizItemProps> = ({ quiz }) => {
  const navigate = useNavigate();

  const handleInfoClick = async () => {
    navigate(`/quizzes/${quiz.id}`);
  };

  return (
    <div className={styles["quiz-item"]}>
      <div className={styles["quiz-content"]}>
        <div className={styles["quiz-title"]}>{quiz.title}</div>
        <div className={styles["quiz-footer"]}>
          <div className={styles["quiz-details"]}>
            <span className={styles["detail-item"]}>
              <span className={styles["detail-label"]}>Date:</span>
              <span className={styles["detail-value"]}>
                {quiz.creationDate.toLocaleString()}
              </span>
            </span>

            <span className={styles["detail-item"]}>
              <span className={styles["detail-label"]}>Max Score:</span>
              <span className={styles["detail-value"]}>
                {quiz.maxPossibleScore}
              </span>
            </span>
          </div>
          <button className={styles["info-button"]} onClick={handleInfoClick}>
            info
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuizItem;
