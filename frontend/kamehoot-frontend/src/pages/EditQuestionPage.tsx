import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Question, QuestionFromBackend, QuestionDTO } from "../types/question";
import QuestionForm from "../components/QuestionForm";
import { ApiService } from "../utils/api";
import styles from "../styles/EditQuestionPage.module.css";

const EditQuestionPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [question, setQuestion] = useState<Question>();

  useEffect(() => {
    const fetchQuestion = async () => {
      try {
        if (id) {
          const data = await ApiService.get<QuestionFromBackend>(
            `/questions/${id}`
          );
          setQuestion({
            ...data,
            creationDate: new Date(data.creationDate.split(".")[0]),
          });
        }
      } catch (error) {
        console.error("Error fetching question:", error);
      }
    };

    fetchQuestion();
  }, [id]);

  const updateQuestion = async (question: Question) => {
    try {
      const questionDTO: QuestionDTO = {
        id: question.id,
        creationDate: new Date().toISOString(),
        questionText: question.questionText,
        category: question.category,
        correctAnswer: question.correctAnswer,
        wrongAnswers: question.wrongAnswers,
        difficulty: question.difficulty,
      };

      await ApiService.put("/questions", questionDTO);
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
