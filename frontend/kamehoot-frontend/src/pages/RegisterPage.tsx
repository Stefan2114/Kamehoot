import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import styles from "../styles/RegisterPage.module.css";

const RegisterPage: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await register({ username, password });
      navigate("/home");
    } catch (error) {
      setError("Registration failed. Username might already be taken.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles["register-container"]}>
      <h2 className={styles["register-title"]}>Register</h2>
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
          {loading ? "Registering..." : "Register"}
        </button>
      </form>
      <p className={styles["login-link"]}>
        Already have an account? <Link to="/login">Login here</Link>
      </p>
    </div>
  );
};

export default RegisterPage;
