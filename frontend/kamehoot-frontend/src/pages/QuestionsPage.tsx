// src/pages/QuestionsPage.tsx

import React, { useEffect, useState, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { Question, QuestionFromBackend } from "../types/question";
import QuestionItem from "../components/QuestionItem";
import styles from "../styles/QuestionsPage.module.css";
import { useOffline } from "../contexts/OfflineContext";
import { ConnectionState, offlineService } from "../services/OfflineService";

const QuestionsPage = () => {
  const navigate = useNavigate();
  const { connectionState } = useOffline();
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loaderRef = useRef(null);

  const [questions, setQuestions] = useState<Question[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(1);
  const [initialLoad, setInitialLoad] = useState(true);

  const [categoryFilter, setCategoryFilter] = useState<string[]>([]);
  const [difficultyFilter, setDifficultyFilter] = useState<number[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [orderBy, setOrderBy] = useState("difficulty");
  const [orderDirection, setOrderDirection] = useState("asc");

  const PAGE_SIZE = 10; // Number of questions to fetch per page

  const fetchQuestions = useCallback(
    async (pageNumber: number, reset: boolean = false) => {
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
      params.append("page", pageNumber.toString());
      params.append("pageSize", PAGE_SIZE.toString());

      try {
        // Use offlineService to fetch questions
        const data = await offlineService.fetchQuestions(params);

        // Parse dates if needed
        const parsed = data.map((q: any) => ({
          ...q,
          creationDate:
            q.creationDate instanceof Date
              ? q.creationDate
              : new Date(
                  typeof q.creationDate === "string"
                    ? q.creationDate.split(".")[0]
                    : q.creationDate
                ),
        }));

        // If there are fewer items than the page size, we've reached the end
        if (parsed.length < PAGE_SIZE) {
          setHasMore(false);
        } else {
          setHasMore(true);
        }

        if (reset) {
          setQuestions(parsed);
        } else {
          setQuestions((prevQuestions) => [...prevQuestions, ...parsed]);
        }

        setInitialLoad(false);
      } catch (error) {
        console.error("Error fetching questions:", error);
      } finally {
        setLoading(false);
      }
    },
    [categoryFilter, difficultyFilter, searchTerm, orderBy, orderDirection]
  );

  // Observer callback for infinite scrolling
  const handleObserver = useCallback(
    (entries: IntersectionObserverEntry[]) => {
      const target = entries[0];
      if (target.isIntersecting && hasMore && !loading) {
        setPage((prevPage) => prevPage + 1);
      }
    },
    [hasMore, loading]
  );

  // Set up the intersection observer
  useEffect(() => {
    const option = {
      root: null,
      rootMargin: "20px",
      threshold: 0,
    };

    observerRef.current = new IntersectionObserver(handleObserver, option);

    if (loaderRef.current) {
      observerRef.current.observe(loaderRef.current);
    }

    return () => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }
    };
  }, [handleObserver]);

  // Fetch categories once
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await offlineService.fetchCategories();
        setCategories(data);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);

  // Reset and fetch questions when filters change
  useEffect(() => {
    setPage(1);
    setHasMore(true);
    fetchQuestions(1, true);
  }, [categoryFilter, difficultyFilter, searchTerm, orderBy, orderDirection]);

  // Fetch additional pages when page number changes
  useEffect(() => {
    if (!initialLoad && page > 1) {
      fetchQuestions(page, false);
    }
  }, [page, initialLoad, fetchQuestions]);

  const handleCategoryFilterChange = (category: string) =>
    setCategoryFilter((prev) =>
      prev.includes(category)
        ? prev.filter((c) => c !== category)
        : [...prev, category]
    );

  const handleDifficultyFilterChange = (difficulty: number) =>
    setDifficultyFilter((prev) =>
      prev.includes(difficulty)
        ? // src/pages/QuestionsPage.tsx (continued)
          prev.filter((c) => c !== difficulty)
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

        {connectionState === ConnectionState.ONLINE && (
          <video className={styles["intro-video"]} controls>
            <source
              src="http://localhost:8081/questions/intro-video"
              type="video/mp4"
            />
            Your browser does not support the video tag.
          </video>
        )}

        {connectionState !== ConnectionState.ONLINE && (
          <div className={styles["offline-message"]}>
            Video not available in offline mode
          </div>
        )}
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

        {initialLoad && loading ? (
          <div className={styles["loading"]}>Loading questions...</div>
        ) : (
          <>
            <div className={styles["questions-list"]}>
              {questions.length > 0 ? (
                questions.map((question) => (
                  <QuestionItem key={question.id} question={question} />
                ))
              ) : (
                <div className={styles["no-questions"]}>No questions found</div>
              )}
            </div>

            {/* Loader element that will trigger the next page load when it comes into view */}
            {hasMore && (
              <div ref={loaderRef} className={styles["loader"]}>
                {loading && <div>Loading more questions...</div>}
              </div>
            )}

            {!hasMore && questions.length > 0 && (
              <div className={styles["end-message"]}>
                No more questions to load
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default QuestionsPage;
