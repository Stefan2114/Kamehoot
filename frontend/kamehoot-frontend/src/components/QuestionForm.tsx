// src/components/QuestionForm.tsx

import React, { useEffect, useState } from "react";
import { Question } from "../types/question";
import styles from "../styles/QuestionForm.module.css";
import { offlineService } from "../services/OfflineService";

interface QuestionFormProps {
  initialQuestion?: Question | null;
  onSubmit: (question: Question) => void;
  mode: "add" | "edit";
}

const QuestionForm: React.FC<QuestionFormProps> = ({
  initialQuestion,
  onSubmit,
  mode = "add",
}) => {
  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await offlineService.fetchCategories();
        setCategories(data);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching categories:", error);
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  const [formData, setFormData] = useState({
    questionText: initialQuestion?.questionText || "",
    category: initialQuestion?.category || "",
    correctAnswer: initialQuestion?.correctAnswer || "",
    wrongAnswers: initialQuestion?.wrongAnswers
      ? [...initialQuestion.wrongAnswers]
      : ["", ""],
    difficulty: initialQuestion?.difficulty || 1,
  });

  useEffect(() => {
    if (initialQuestion) {
      setFormData({
        questionText: initialQuestion.questionText,
        category: initialQuestion.category,
        correctAnswer: initialQuestion.correctAnswer,
        wrongAnswers:
          initialQuestion.wrongAnswers.length >= 2
            ? initialQuestion.wrongAnswers
            : [...initialQuestion.wrongAnswers, "", ""].slice(0, 2),
        difficulty: initialQuestion.difficulty,
      });
    } else if (categories.length > 0 && !formData.category) {
      // Set default category when categories are loaded
      setFormData((prev) => ({
        ...prev,
        category: categories[0],
      }));
    }
  }, [initialQuestion, categories]);

  if (loading) {
    return <div>Loading form...</div>;
  }

  if (categories.length === 0) {
    return <div>Sorry, no categories available to create a question</div>;
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
      id: initialQuestion?.id || 0, // server will assign real ID for new questions
      questionText: formData.questionText.trim(),
      category: formData.category,
      correctAnswer: formData.correctAnswer.trim(),
      wrongAnswers: formData.wrongAnswers.map((answer) => answer.trim()),
      difficulty: Number(formData.difficulty),
      creationDate: initialQuestion?.creationDate || new Date(),
    };

    onSubmit(submittedQuestion);
  };

  return (
    <div className={styles["question-form-container"]}>
      <form onSubmit={handleSubmit} className={styles["question-form"]}>
        <div className={styles["form-content"]}>
          <div className={styles["form-sidebar"]}>
            <div className={styles["category-section"]}>
              <h3>Category</h3>
              <div className={styles["options"]}>
                {categories.map((cat) => (
                  <div key={cat} className={styles["option"]}>
                    <input
                      type="radio"
                      id={`category-${cat}`}
                      value={cat}
                      name="category"
                      checked={formData.category === cat}
                      onChange={handleInputChange}
                    />
                    <label htmlFor={`category-${cat}`}>{cat}</label>
                  </div>
                ))}
              </div>
            </div>

            <div className={styles["difficulty-section"]}>
              <h3>Difficulty</h3>
              <div className={styles["options"]}>
                {[1, 2, 3].map((diff) => (
                  <div key={diff} className={styles["option"]}>
                    <input
                      type="radio"
                      id={`difficulty-${diff}`}
                      value={diff}
                      name="difficulty"
                      checked={formData.difficulty === diff}
                      onChange={handleInputChange}
                    />
                    <label htmlFor={`difficulty-${diff}`}>
                      {diff === 1 ? "Easy" : diff === 2 ? "Medium" : "Hard"}
                    </label>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className={styles["form-main-content"]}>
            <div className={styles["question-section"]}>
              <h3>Question:</h3>
              <textarea
                name="questionText"
                placeholder="type..."
                value={formData.questionText}
                onChange={handleInputChange}
                className={styles["question-text-input"]}
              />
            </div>

            <div className={styles["question-section"]}>
              <label>Correct answer:</label>
              <textarea
                name="correctAnswer"
                placeholder="type..."
                value={formData.correctAnswer}
                onChange={handleInputChange}
                className={styles["answer-input"]}
              />
            </div>

            {[0, 1].map((index) => (
              <div key={index} className={styles["question-section"]}>
                <label>Wrong answer:</label>
                <textarea
                  placeholder="type..."
                  value={formData.wrongAnswers[index] || ""}
                  onChange={(e) =>
                    handleWrongAnswerChange(index, e.target.value)
                  }
                  className={styles["answer-input"]}
                />
              </div>
            ))}

            <button type="submit" className={styles["submit-button"]}>
              {mode === "add" ? "Add Question" : "Update Question"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default QuestionForm;
