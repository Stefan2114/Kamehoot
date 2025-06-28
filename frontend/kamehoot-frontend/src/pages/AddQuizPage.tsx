import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import { QuizDTO } from "../types/quiz";
import { ApiService } from "../services/apiService";
import styles from "../styles/AddQuizPage.module.css";

interface QuizRequest {
  title: string;
  description: string;
  creationDate: string;
  questionIds: string[];
}

const AddQuizPage = () => {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState<Question[]>([]);
  const [selectedQuestions, setSelectedQuestions] = useState<Question[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [formData, setFormData] = useState({
    title: "",
    description: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchQuestions = useCallback(async () => {
    try {
      const params = new URLSearchParams();
      if (searchTerm) {
        params.append("searchTerm", searchTerm);
      }
      const data = await ApiService.get<Question[]>(
        `/questions?${params.toString()}`
      );
      setQuestions(data);
    } catch (error) {
      console.error("Error fetching questions:", error);
    }
  }, [searchTerm]);

  useEffect(() => {
    fetchQuestions();
  }, [fetchQuestions]);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleQuestionToggle = (question: Question) => {
    setSelectedQuestions((prev) => {
      const isSelected = prev.some((q) => q.id === question.id);
      if (isSelected) {
        return prev.filter((q) => q.id !== question.id);
      } else {
        return [...prev, question];
      }
    });
  };

  const calculateMaxScore = () => {
    return selectedQuestions.reduce((total, question) => total + 1, 0); // change this to be + question.difficulty
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.title.trim() || !formData.description.trim()) {
      alert("Please fill in all required fields.");
      return;
    }

    if (selectedQuestions.length === 0) {
      alert("Please select at least one question.");
      return;
    }

    setIsSubmitting(true);

    try {
      const quizRequest: QuizRequest = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        creationDate: new Date().toISOString(),
        questionIds: selectedQuestions.map((q) => q.id),
      };

      await ApiService.post("/quizzes", quizRequest);
      alert("Quiz created successfully!");
      navigate("/quizzes");
    } catch (error) {
      console.error("Error creating quiz:", error);
      alert("An error occurred while creating the quiz.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    if (
      window.confirm(
        "Are you sure you want to cancel? All unsaved changes will be lost."
      )
    ) {
      navigate("/quizzes");
    }
  };

  return (
    <div className={styles["add-quiz-container"]}>
      <div className={styles["add-quiz-content"]}>
        <h2>Create New Quiz</h2>

        <form onSubmit={handleSubmit}>
          <div className={styles["form-section"]}>
            <h3>Quiz Details</h3>
            <div className={styles["input-group"]}>
              <label htmlFor="title">Title *</label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                className={styles["form-input"]}
                required
                maxLength={100}
              />
            </div>

            <div className={styles["input-group"]}>
              <label htmlFor="description">Description *</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                className={styles["form-textarea"]}
                required
                maxLength={500}
                rows={4}
              />
            </div>
          </div>

          <div className={styles["form-section"]}>
            <h3>Select Questions</h3>
            <div className={styles["search-section"]}>
              <input
                type="text"
                placeholder="Search questions..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className={styles["search-input"]}
              />
            </div>

            <div className={styles["selected-summary"]}>
              <span>Selected Questions: {selectedQuestions.length}</span>
              <span>Max Possible Score: {calculateMaxScore()}</span>
            </div>

            <div className={styles["questions-list"]}>
              {questions.map((question) => {
                const isSelected = selectedQuestions.some(
                  (q) => q.id === question.id
                );
                return (
                  <div
                    key={question.id}
                    className={`${styles["question-item"]} ${
                      isSelected ? styles["selected"] : ""
                    }`}
                    onClick={() => handleQuestionToggle(question)}
                  >
                    <div className={styles["question-checkbox"]}>
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleQuestionToggle(question)}
                        onClick={(e) => e.stopPropagation()}
                      />
                    </div>
                    <div className={styles["question-content"]}>
                      <div className={styles["question-text"]}>
                        {question.questionText}
                      </div>
                      <div className={styles["question-meta"]}>
                        <span className={styles["question-category"]}>
                          {question.category}
                        </span>
                        <span className={styles["question-difficulty"]}>
                          Difficulty: {question.difficulty}
                        </span>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {questions.length === 0 && (
              <div className={styles["no-questions"]}>
                No questions found. Try adjusting your search terms.
              </div>
            )}
          </div>

          <div className={styles["selected-questions-preview"]}>
            <h3>Selected Questions Preview</h3>
            {selectedQuestions.length === 0 ? (
              <p className={styles["no-selection"]}>
                No questions selected yet.
              </p>
            ) : (
              <div className={styles["preview-list"]}>
                {selectedQuestions.map((question, index) => (
                  <div key={question.id} className={styles["preview-item"]}>
                    <div className={styles["preview-header"]}>
                      <span>Question {index + 1}</span>
                      <button
                        type="button"
                        onClick={() => handleQuestionToggle(question)}
                        className={styles["remove-button"]}
                      >
                        Remove
                      </button>
                    </div>
                    <div className={styles["preview-text"]}>
                      {question.questionText}
                    </div>
                    <div className={styles["preview-answer"]}>
                      <strong>Correct Answer:</strong> {question.correctAnswer}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className={styles["form-actions"]}>
            <button
              type="button"
              onClick={handleCancel}
              className={styles["cancel-button"]}
              disabled={isSubmitting}
            >
              Cancel
            </button>
            <button
              type="submit"
              className={styles["submit-button"]}
              disabled={isSubmitting || selectedQuestions.length === 0}
            >
              {isSubmitting ? "Creating..." : "Create Quiz"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddQuizPage;
