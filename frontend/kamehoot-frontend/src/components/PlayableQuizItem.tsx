import { useNavigate } from "react-router-dom";
import { Quiz } from "../types/quiz";
import styles from "../styles/PlayableQuizItem.module.css";

interface PlayableQuizItemProps {
  quiz: Quiz;
}

const PlayableQuizItem: React.FC<PlayableQuizItemProps> = ({ quiz }) => {
  const navigate = useNavigate();

  const handlePlayQuiz = () => {
    navigate(`/play/${quiz.id}`);
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  return (
    <div className={styles["quiz-card"]}>
      <div className={styles["quiz-header"]}>
        <h3 className={styles["quiz-title"]}>{quiz.title}</h3>
      </div>

      <div className={styles["quiz-description"]}>
        <p>{quiz.description}</p>
      </div>

      <div className={styles["quiz-stats"]}>
        <div className={styles["stat-item"]}>
          <span className={styles["stat-icon"]}>📊</span>
          <div className={styles["stat-info"]}>
            <span className={styles["stat-label"]}>Max Score</span>
            <span className={styles["stat-value"]}>
              {quiz.maxPossibleScore}
            </span>
          </div>
        </div>

        <div className={styles["stat-item"]}>
          <span className={styles["stat-icon"]}>❓</span>
          <div className={styles["stat-info"]}>
            <span className={styles["stat-label"]}>Questions</span>
            <span className={styles["stat-value"]}>
              {quiz.questions.length}
            </span>
          </div>
        </div>

        <div className={styles["stat-item"]}>
          <span className={styles["stat-icon"]}>📅</span>
          <div className={styles["stat-info"]}>
            <span className={styles["stat-label"]}>Created</span>
            <span className={styles["stat-value"]}>
              {formatDate(quiz.creationDate)}
            </span>
          </div>
        </div>
      </div>

      <div className={styles["quiz-actions"]}>
        <button className={styles["play-button"]} onClick={handlePlayQuiz}>
          <span className={styles["button-icon"]}>▶️</span>
          Start Quiz
        </button>
      </div>
    </div>
  );
};

export default PlayableQuizItem;
