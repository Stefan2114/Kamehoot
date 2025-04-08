import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Question } from "../types/question";
import QuestionForm from "../components/QuestionForm";
import styles from "../styles/EditQuestionPage.module.css";

const EditQuestionPage = () => {
  const { id } = useParams<{ id: string }>();

  console.log(id);

  const navigate = useNavigate();

  const [question, setQuestion] = useState<Question>();

  useEffect(() => {
    fetch(`http://localhost:8081/questions/${id}`)
      .then((response) => response.json())
      .then((data: Question) => setQuestion(data))
      .catch((error) => console.error("Error fetching question:", error));
  }, []);

  const updateQuestion = async (question: Question) => {
    try {
      const response = await fetch("http://localhost:8081/questions", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(question),
      });

      if (!response.ok) {
        throw new Error("Failed to add question");
      }

      console.log("Question updated successfully!");
      navigate("/questions");
    } catch (error) {
      console.error("Error updating question:", error);
      alert("An error occurred while updating the question.");
    }
  };

  const handleUpdateQuestion = (updatedQuestion: Question) => {
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
    updateQuestion(updatedQuestion);
  };

  return (
    <div className={styles["edit-question-page-container"]}>
      <QuestionForm
        initialQuestion={question}
        onSubmit={handleUpdateQuestion}
        mode="edit"
      />
    </div>
  );
};

export default EditQuestionPage;
