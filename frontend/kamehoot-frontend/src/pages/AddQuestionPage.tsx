// src/pages/AddQuestionPage.tsx

import React from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import QuestionForm from "../components/QuestionForm";
import styles from "../styles/AddQuestionPage.module.css";
import { offlineService } from "../services/OfflineService";

const AddQuestionPage: React.FC = () => {
  const navigate = useNavigate();

  const handleAddQuestion = async (newQuestion: Question) => {
    if (!newQuestion.questionText.trim()) {
      alert("Question text is required!");
      return;
    }

    if (!newQuestion.correctAnswer.trim()) {
      alert("Correct answer is required!");
      return;
    }

    const validWrongAnswers = newQuestion.wrongAnswers.filter(
      (answer) => answer.trim() !== ""
    );

    if (validWrongAnswers.length !== 2) {
      alert("2 wrong answers are required for multiple-choice questions");
      return;
    }

    try {
      const result = await offlineService.createQuestion(newQuestion);
      if (result) {
        console.log("Question added successfully!");
        navigate("/questions");
      } else {
        throw new Error("Failed to add question");
      }
    } catch (error) {
      console.error("Error adding question:", error);
      alert("An error occurred while adding the question.");
    }
  };

  return (
    <div className={styles["add-question-page-container"]}>
      <QuestionForm onSubmit={handleAddQuestion} mode="add" />
    </div>
  );
};

export default AddQuestionPage;
