import React, { useEffect, useState } from "react";
import { Question } from "../types/question";
import "../styles/QuestionForm.css";

interface QuestionFormProps {
  initialQuestion?: Question;
  onSubmit: (question: Question) => void;
  mode: "add" | "edit";
}

const QuestionForm: React.FC<QuestionFormProps> = ({
  initialQuestion,
  onSubmit,
  mode = "add",
}) => {
  const [categories, setCategories] = useState<string[]>([]);

  useEffect(() => {
    fetch("http://localhost:8081/categories")
      .then((response) => response.json())
      .then((data: string[]) => setCategories(data))
      .catch((error) => console.error("Error fetching messages:", error));
  }, []);

  const [formData, setFormData] = useState({
    questionText: initialQuestion?.questionText || "",
    category: initialQuestion?.category || categories[0],
    correctAnswer: initialQuestion?.correctAnswer || "",
    wrongAnswers: initialQuestion?.wrongAnswers
      ? [...initialQuestion.wrongAnswers, "", ""]
      : ["", ""],
    difficulty: initialQuestion?.difficulty || 1,
  });

  useEffect(() => {
    if (initialQuestion) {
      setFormData({
        questionText: initialQuestion.questionText,
        category: initialQuestion.category,
        correctAnswer: initialQuestion.correctAnswer,
        wrongAnswers: initialQuestion.wrongAnswers,
        difficulty: initialQuestion.difficulty,
      });
    }
  }, [initialQuestion]);

  if (categories.length == 0) {
    return <div>Sorry no categories available to create a question</div>;
  }

  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;

    const processedValue = name === "difficulty" ? Number(value) : value;

    setFormData((prev) => ({
      ...prev,
      [name]: processedValue,
    }));
  };

  const handleWrongAnswerChange = (index: number, value: string) => {
    const updatedWrongAnswers = [...formData.wrongAnswers];
    updatedWrongAnswers[index] = value;
    setFormData((prev) => ({ ...prev, wrongAnswers: updatedWrongAnswers }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.questionText.trim()) {
      alert("Question text is required!");
      return;
    }

    if (!formData.correctAnswer.trim()) {
      alert("Correct answer is required!");
      return;
    }

    const validWrongAnswers = formData.wrongAnswers.filter(
      (answer) => answer.trim() !== ""
    );
    if (validWrongAnswers.length !== 2) {
      alert("2 wrong answers are required for multiple-choice questions");
      return;
    }

    const submittedQuestion: Question = {
      id: initialQuestion?.id || 0,
      questionText: formData.questionText.trim(),
      category: formData.category,
      correctAnswer: formData.correctAnswer.trim(),
      wrongAnswers: formData.wrongAnswers.map((answer) => answer.trim()),
      difficulty: Number(formData.difficulty),
      date: new Date(),
    };

    onSubmit(submittedQuestion);
  };

  return (
    <div className="question-form-container">
      <form onSubmit={handleSubmit} className="question-form">
        <div className="form-content">
          <div className="form-sidebar">
            <div className="category-section">
              <h3>Category</h3>
              <div className="options">
                {categories.map((cat) => (
                  <div key={cat} className="option">
                    <input
                      type="radio"
                      id={`category-${cat}`}
                      value={cat}
                      checked={formData.category === cat}
                      onChange={handleInputChange}
                    />
                    <label>{cat}</label>
                  </div>
                ))}
              </div>
            </div>

            <div className="difficulty-section">
              <h3>Difficulty</h3>
              <div className="options">
                {[1, 2, 3].map((diff) => (
                  <div key={diff} className="option">
                    <input
                      type="radio"
                      id={`difficulty-${diff}`}
                      value={diff}
                      checked={formData.difficulty === diff}
                      onChange={handleInputChange}
                    />
                    <label>
                      {diff === 1 ? "Easy" : diff === 2 ? "Medium" : "Hard"}
                    </label>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="form-main-content">
            <div className="question-section">
              <h3>Question:</h3>
              <textarea
                name="questionText"
                placeholder="type..."
                value={formData.questionText}
                onChange={handleInputChange}
                className="question-text-input"
              />
            </div>

            <div className="question-section">
              <label>Correct answer:</label>
              <textarea
                name="correctAnswer"
                placeholder="type..."
                value={formData.correctAnswer}
                onChange={handleInputChange}
                className="answer-input"
              />
            </div>

            {[0, 1].map((index) => (
              <div className="question-section">
                <label>Wrong answer:</label>
                <textarea
                  placeholder="type..."
                  value={formData.wrongAnswers[index]}
                  onChange={(e) =>
                    handleWrongAnswerChange(index, e.target.value)
                  }
                  className="answer-input"
                />
              </div>
            ))}

            <button type="submit" className="submit-button">
              {mode === "add" ? "Add Question" : "Update Question"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default QuestionForm;
