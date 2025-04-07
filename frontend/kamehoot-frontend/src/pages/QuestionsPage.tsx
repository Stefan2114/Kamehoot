import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Question, QuestionFromBackend } from "../types/question";
import QuestionItem from "../components/QuestionItem";
import "../styles/QuestionsPage.css";

const QuestionsPage = () => {
  const navigate = useNavigate();

  const [questions, setQuestions] = useState<Question[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const [categoryFilter, setCategoryFilter] = useState<string[]>([]);
  const [difficultyFilter, setDifficultyFilter] = useState<number[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("difficulty");
  const [orderDirection, setOrderDirection] = useState("asc");

  const fetchQuestions = () => {
    setLoading(true);

    // Build query parameters
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

    // Make API request
    fetch(`http://localhost:8081/questions?${params.toString()}`)
      .then((response) => response.json())
      .then((data: QuestionFromBackend[]) => {
        const parsed = data.map((q) => ({
          ...q,
          creationDate: new Date(q.creationDate.split(".")[0]), // ✅ this works now
        }));
        setQuestions(parsed);
      })
      .catch((error) => {
        console.error("Error fetching questions:", error);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetch("http://localhost:8081/categories")
      .then((response) => response.json())
      .then((data: string[]) => setCategories(data))
      .catch((error) => console.error("Error fetching messages:", error));
  }, []);

  useEffect(() => {
    fetchQuestions();
  }, [categoryFilter, difficultyFilter, searchTerm, orderBy, orderDirection]);

  const filteredQuestions = useMemo(() => {
    return questions.filter((question) => {
      const categoryMatch =
        categoryFilter.length === 0 ||
        categoryFilter.includes(question.category);

      const difficultyMatch =
        difficultyFilter.length === 0 ||
        difficultyFilter.includes(question.difficulty);

      const searchMatch = question.questionText
        .toLowerCase()
        .includes(searchTerm.toLowerCase());

      return categoryMatch && difficultyMatch && searchMatch;
    });
  }, [questions, categoryFilter, difficultyFilter, searchTerm]);

  const sortedQuestions = useMemo(() => {
    return [...filteredQuestions].sort((a, b) => {
      switch (orderBy) {
        case "difficulty":
          return a.difficulty - b.difficulty;
        case "date":
          return (
            new Date(a.creationDate).getDate() -
            new Date(b.creationDate).getDate()
          );
        default:
          return a.id - b.id;
      }
    });
  }, [filteredQuestions, orderBy]);

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

  const handleOrderByChange = (e: React.ChangeEvent<HTMLSelectElement>) =>
    setOrderBy(e.target.value);

  const handleAddQuestion = () => {
    navigate("/questions/add");
  };

  return (
    <div className="questions-container">
      <div className="sidebar">
        <button className="add-button" onClick={handleAddQuestion}>
          Add Question
        </button>

        <div className="filter-section">
          <h3>Categories</h3>
          {categories.map((category) => (
            <div key={category} className="filter-checkbox">
              <input
                type="checkbox"
                id={`category-${category}`}
                checked={categoryFilter.includes(category)}
                onChange={() => handleCategoryFilterChange(category)}
              />
              <label>{category}</label>
            </div>
          ))}
        </div>

        <div className="filter-section">
          <h3>Difficulties</h3>
          {[1, 2, 3].map((difficulty) => (
            <div key={difficulty} className="filter-checkbox">
              <input
                type="checkbox"
                id={`difficulty-${difficulty}`}
                checked={difficultyFilter.includes(difficulty)}
                onChange={() => handleDifficultyFilterChange(difficulty)}
              />
              <label>
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

      <div className="main-content">
        <div className="search-and-sort">
          <div className="search-container">
            <input
              type="text"
              placeholder="Search questions..."
              value={searchTerm}
              onChange={handleSearchChange}
            />
          </div>
          <select
            className="order-by-select"
            value={orderBy}
            onChange={handleOrderByChange}
          >
            <option value="difficulty">Sort by Difficulty</option>
            <option value="date">Sort by Date</option>
          </select>
        </div>
        {loading ? (
          <div className="loading">Loading questions...</div>
        ) : (
          <div className="questions-list">
            {questions.map((question) => (
              <QuestionItem key={question.id} question={question} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default QuestionsPage;
