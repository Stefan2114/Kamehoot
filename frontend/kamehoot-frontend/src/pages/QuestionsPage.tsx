import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import QuestionItem from "../components/QuestionItem";
import { ApiService } from "../services/apiService";
import { useAuth } from "../contexts/AuthContext";
import styles from "../styles/QuestionsPage.module.css";

const QuestionsPage = () => {
  const navigate = useNavigate();

  const [questions, setQuestions] = useState<Question[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [categoryFilter, setCategoryFilter] = useState<string[]>([]);
  const [difficultyFilter, setDifficultyFilter] = useState<number[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("creationDate");
  const [orderDirection, setOrderDirection] = useState("desc");

  const fetchQuestions = useCallback(async () => {
    try {
      const params = new URLSearchParams();

      if (categoryFilter.length > 0) {
        params.append("categories", categoryFilter.join(","));
      }

      if (difficultyFilter.length > 0) {
        params.append("difficulties", difficultyFilter.join(","));
      }

      if (searchTerm) {
        params.append("searchTerm", searchTerm);
      }

      params.append("orderBy", orderBy);
      params.append("orderDirection", orderDirection);

      const data = await ApiService.get<Question[]>(
        `/questions?${params.toString()}`
      );

      setQuestions(data);
    } catch (error) {
      console.error("Error fetching questions:", error);
    }
  }, [categoryFilter, difficultyFilter, searchTerm, orderBy, orderDirection]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await ApiService.get<string[]>("/categories");
        setCategories(data);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);

  useEffect(() => {
    fetchQuestions();
  }, [fetchQuestions]);

  const handleCategoryFilterChange = (category: string) =>
    setCategoryFilter((prev) =>
      prev.includes(category)
        ? prev.filter((c) => c !== category)
        : [...prev, category]
    );

  const handleDifficultyFilterChange = (difficulty: number) =>
    setDifficultyFilter((prev) =>
      prev.includes(difficulty)
        ? prev.filter((c) => c !== difficulty)
        : [...prev, difficulty]
    );

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) =>
    setSearchTerm(e.target.value);

  const handleOrderByChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setOrderBy(e.target.value);
    if (orderBy === "creationDate") {
      setOrderDirection("desc");
    } else {
      setOrderDirection("asc");
    }
  };

  const handleAddQuestion = () => {
    navigate("/questions/add");
  };

  return (
    <div className={styles["questions-container"]}>
      <div className={styles["sidebar"]}>
        <div
          style={{
            marginBottom: "20px",
            display: "flex",
            gap: "10px",
            flexDirection: "column",
          }}
        ></div>

        <button className={styles["add-button"]} onClick={handleAddQuestion}>
          Add Question
        </button>

        <div className={styles["filter-section"]}>
          <h3>Categories</h3>
          {categories.map((category) => (
            <div key={category} className={styles["filter-checkbox"]}>
              <input
                type="checkbox"
                id={`category-${category}`}
                checked={categoryFilter.includes(category)}
                onChange={() => handleCategoryFilterChange(category)}
              />
              <label htmlFor={`category-${category}`}>{category}</label>
            </div>
          ))}
        </div>

        <div className={styles["filter-section"]}>
          <h3>Difficulties</h3>
          {[1, 2, 3].map((difficulty) => (
            <div key={difficulty} className={styles["filter-checkbox"]}>
              <input
                type="checkbox"
                id={`difficulty-${difficulty}`}
                checked={difficultyFilter.includes(difficulty)}
                onChange={() => handleDifficultyFilterChange(difficulty)}
              />
              <label htmlFor={`difficulty-${difficulty}`}>
                {difficulty === 1
                  ? "Easy"
                  : difficulty === 2
                  ? "Medium"
                  : "Hard"}
              </label>
            </div>
          ))}
        </div>
      </div>

      <div className={styles["main-content"]}>
        <div className={styles["search-and-sort"]}>
          <div className={styles["search-container"]}>
            <input
              type="text"
              placeholder="Search questions..."
              value={searchTerm}
              onChange={handleSearchChange}
            />
          </div>
          <select
            className={styles["order-by-select"]}
            value={orderBy}
            onChange={handleOrderByChange}
          >
            <option value="difficulty">Sort by Difficulty</option>
            <option value="creationDate">Sort by Date</option>
          </select>
        </div>

        <div className={styles["questions-list"]}>
          {questions.length > 0 ? (
            questions.map((question) => (
              <QuestionItem key={question.id} question={question} />
            ))
          ) : (
            <div className={styles["no-questions"]}>No questions found</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default QuestionsPage;
