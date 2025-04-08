// src/pages/EditQuestionPage.tsx

import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Question } from "../types/question";
import QuestionForm from "../components/QuestionForm";
import styles from "../styles/EditQuestionPage.module.css";
import { offlineService } from "../services/OfflineService";
import { useOffline } from "../contexts/OfflineContext";
import { ConnectionState } from "../services/OfflineService";

const EditQuestionPage = () => {
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

  const handleUpdateQuestion = async (updatedQuestion: Question) => {
    if (!updatedQuestion.questionText.trim()) {
      alert("Question text is required!");
      return;
    }

    if (!updatedQuestion.correctAnswer.trim()) {
      alert("Correct answer is required!");
      return;
    }

    const validWrongAnswers = updatedQuestion.wrongAnswers.filter(
      (answer) => answer.trim() !== ""
    );

    if (validWrongAnswers.length !== 2) {
      alert("2 wrong answers are required for multiple-choice questions");
      return;
    }

    try {
      const success = await offlineService.updateQuestion(updatedQuestion);
      if (success) {
        console.log("Question updated successfully!");
        navigate(`/questions/${updatedQuestion.id}`);
      } else {
        throw new Error("Failed to update question");
      }
    } catch (error) {
      console.error("Error updating question:", error);
      alert("An error occurred while updating the question.");
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
    <div className={styles["edit-question-page-container"]}>
      {offlineMessage}
      <QuestionForm
        initialQuestion={question}
        onSubmit={handleUpdateQuestion}
        mode="edit"
      />
    </div>
  );
};

export default EditQuestionPage;
