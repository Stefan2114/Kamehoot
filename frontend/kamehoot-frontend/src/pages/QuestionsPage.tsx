import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Question } from "../types/question";
import QuestionItem from "../components/QuestionItem";
import "../styles/QuestionsPage.css";

const QuestionsPage = () => {
  const navigate = useNavigate();

  const [questions, setQuestions] = useState<Question[]>([]);
  const [categories, setCategories] = useState<string[]>([]);

  const [categoryFilter, setCategoryFilter] = useState<string[]>([]);
  const [difficultyFilter, setDifficultyFilter] = useState<number[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("difficulty");
  const [currentPage, setCurrentPage] = useState(1);
  const questionsPerPage = 5;

  useEffect(() => {
    fetch("http://localhost:8081/questions")
      .then((response) => response.json())
      .then((data: Question[]) => setQuestions(data))
      .catch((error) => console.error("Error fetching questions:", error));
  }, []);

  useEffect(() => {
    fetch("http://localhost:8081/categories")
      .then((response) => response.json())
      .then((data: string[]) => setCategories(data))
      .catch((error) => console.error("Error fetching categories:", error));
  }, []);

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
          return new Date(a.date).getDate() - new Date(b.date).getDate();
        default:
          return a.id - b.id;
      }
    });
  }, [filteredQuestions, orderBy]);

  const paginatedQuestions = useMemo(() => {
    const startIndex = (currentPage - 1) * questionsPerPage;
    return sortedQuestions.slice(startIndex, startIndex + questionsPerPage);
  }, [sortedQuestions, currentPage, questionsPerPage]);

  const totalPages = Math.ceil(sortedQuestions.length / questionsPerPage);

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const renderPagination = () => {
    const pages: React.ReactNode[] = [];
    ``;
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
    setCurrentPage(1);
  };

  const handleDifficultyFilterChange = (difficulty: number) => {
    setDifficultyFilter((prev) =>
      prev.includes(difficulty)
        ? prev.filter((c) => c !== difficulty)
        : [...prev, difficulty]
    );
    setCurrentPage(1);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setCurrentPage(1);
  };

  const handleOrderByChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setOrderBy(e.target.value);
    setCurrentPage(1);
  };

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
        <div className="questions-list">
          {paginatedQuestions.map((question) => (
            <QuestionItem key={question.id} question={question} />
          ))}
        </div>

        {totalPages > 1 && (
          <div className="pagination">{renderPagination()}</div>
        )}
      </div>
    </div>
  );
};

export default QuestionsPage;
