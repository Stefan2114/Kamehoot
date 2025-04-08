import React from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import QuestionForm from "../components/QuestionForm";
import styles from "../styles/AddQuestionPage.module.css";

const AddQuestionPage: React.FC = () => {
  const navigate = useNavigate();

  const addQuestion = async (question: Question) => {
    try {
      const response = await fetch("http://localhost:8081/questions", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(question),
      });

      if (!response.ok) {
        throw new Error("Failed to add question");
      }

      console.log("Question added successfully!");
      navigate("/questions");
    } catch (error) {
      console.error("Error adding question:", error);
      alert("An error occurred while adding the question.");
    }
  };

  const handleAddQuestion = (newQuestion: Question) => {
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
    console.log(newQuestion);
    addQuestion(newQuestion);
  };

  return (
    <div className={styles["add-question-page-container"]}>
      <QuestionForm onSubmit={handleAddQuestion} mode="add" />
    </div>
  );
};

export default AddQuestionPage;
