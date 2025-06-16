import React from "react";
import { useNavigate } from "react-router-dom";
import { Question, QuestionDTO } from "../types/question";
import QuestionForm from "../components/QuestionForm";
import { ApiService } from "../services/api";
import styles from "../styles/AddQuestionPage.module.css";

const AddQuestionPage: React.FC = () => {
  const navigate = useNavigate();

  const addQuestion = async (question: Question) => {
    try {
      const questionDTO: QuestionDTO = {
        creationDate: new Date().toISOString(),
        questionText: question.questionText,
        category: question.category,
        correctAnswer: question.correctAnswer,
        wrongAnswers: question.wrongAnswers,
        difficulty: question.difficulty,
      };

      await ApiService.post("/questions", questionDTO);
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
