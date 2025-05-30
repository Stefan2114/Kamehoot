import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import styles from "../styles/LoginPage.module.css";

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await login({ username, password });
      navigate("/questions");
    } catch (error) {
      setError("Invalid username or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles["login-container"]}>
      <h2 className={styles["login-title"]}>Login</h2>
      <form onSubmit={handleSubmit}>
        <div className={styles["form-group"]}>
          <label className={styles["form-label"]}>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            className={styles["form-input"]}
          />
        </div>
        <div className={styles["form-group"]}>
          <label className={styles["form-label"]}>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className={styles["form-input"]}
          />
        </div>
        {error && <div className={styles["error-message"]}>{error}</div>}
        <button
          type="submit"
          disabled={loading}
          className={styles["submit-button"]}
        >
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>
      <p className={styles["register-link"]}>
        Don't have an account? <Link to="/register">Register here</Link>
      </p>
    </div>
  );
};

export default LoginPage;
