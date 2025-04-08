import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import QuestionItem from "../components/QuestionItem";
import styles from "../styles/QuestionsPage.module.css";

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

  // Pagination (kept in frontend)
  const [currentPage, setCurrentPage] = useState(1);
  const [totalQuestions, setTotalQuestions] = useState(0);
  const questionsPerPage = 5;

  // Fetch filtered questions from backend
  const fetchQuestions = () => {
    setLoading(true);

    setTimeout(() => {}, 10000);

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

    // Add pagination parameters
    params.append("page", currentPage.toString());
    params.append("limit", questionsPerPage.toString());

    // Make API request
    fetch(`http://localhost:8081/questions?${params.toString()}`)
      .then((response) => response.json())
      .then((data) => {
        setQuestions(data.questions);
        setTotalQuestions(data.total);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching questions:", error);
        setLoading(false);
      });
  };

  // Fetch categories
  useEffect(() => {
    fetch("http://localhost:8081/categories")
      .then((response) => response.json())
      .then((data: string[]) => setCategories(data))
      .catch((error) => console.error("Error fetching categories:", error));
  }, []);

  // Fetch questions whenever filters change
  useEffect(() => {
    fetchQuestions();
  }, [
    categoryFilter,
    difficultyFilter,
    searchTerm,
    orderBy,
    orderDirection,
    currentPage,
  ]);

  const totalPages = Math.ceil(totalQuestions / questionsPerPage);

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const renderPagination = () => {
    const pages: React.ReactNode[] = [];
    const maxPagesToShow = 5;

    const addPageButton = (pageNumber: number) => {
      pages.push(
        <button
          key={pageNumber}
          onClick={() => handlePageChange(pageNumber)}
          className={currentPage === pageNumber ? "active-page" : ""}
        >
          {pageNumber}
        </button>
      );
    };

    const addEllipsis = (key: string) => {
      pages.push(
        <span key={key} className="pagination-ellipsis">
          ...
        </span>
      );
    };

    if (totalPages <= maxPagesToShow) {
      for (let i = 1; i <= totalPages; i++) {
        addPageButton(i);
      }
    } else {
      addPageButton(1);

      let startPage = Math.max(2, currentPage - 1);
      let endPage = Math.min(totalPages - 1, currentPage + 1);

      if (currentPage > 3) {
        addEllipsis("start-ellipsis");
      }

      for (let i = startPage; i <= endPage; i++) {
        addPageButton(i);
      }

      if (currentPage < totalPages - 2) {
        addEllipsis("end-ellipsis");
      }

      addPageButton(totalPages);
    }

    return pages;
  };

  const handleCategoryFilterChange = (category: string) => {
    setCategoryFilter((prev) =>
      prev.includes(category)
        ? prev.filter((c) => c !== category)
        : [...prev, category]
    );
    setCurrentPage(1); // Reset to first page when filter changes
  };

  const handleDifficultyFilterChange = (difficulty: number) => {
    setDifficultyFilter((prev) =>
      prev.includes(difficulty)
        ? prev.filter((c) => c !== difficulty)
        : [...prev, difficulty]
    );
    setCurrentPage(1); // Reset to first page when filter changes
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setCurrentPage(1); // Reset to first page when search changes
  };

  const handleOrderByChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setOrderBy(e.target.value);
    setCurrentPage(1); // Reset to first page when sort changes
  };

  const handleAddQuestion = () => {
    navigate("/questions/add");
  };

  return (
    <div className={styles["questions-container"]}>
      <div className={styles["sidebar"]}>
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
              <label>{category}</label>
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
            <option value="date">Sort by Date</option>
          </select>
        </div>

        {loading ? (
          <div className={styles["loading"]}>Loading questions...</div>
        ) : (
          <div className={styles["questions-list"]}>
            {questions.length > 0 ? (
              questions.map((question) => (
                <QuestionItem key={question.id} question={question} />
              ))
            ) : (
              <div className={styles["no-questions"]}>
                No questions found matching your criteria.
              </div>
            )}
          </div>
        )}

        {totalPages > 1 && (
          <div className={styles["pagination"]}>{renderPagination()}</div>
        )}
      </div>
    </div>
  );
};

export default QuestionsPage;
