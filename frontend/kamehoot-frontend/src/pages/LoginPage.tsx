import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import styles from "../styles/LoginPage.module.css";

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [totpCode, setTotpCode] = useState("");
  const [requires2FA, setRequires2FA] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const credentials = {
        username,
        password,
        ...(totpCode && { totpCode: parseInt(totpCode) }),
      };

      const response = await login(credentials);
      if (response.requires2FA) {
        setRequires2FA(true);
        setError("Please enter your 2FA code");
      } else {
        navigate("/home");
      }
    } catch (error: any) {
      console.error(error);
      await new Promise((resolve) => setTimeout(resolve, 5000)); // Uncomment to simulate 5s delay
      if (error.message.includes("Invalid 2FA code")) {
        setError("Invalid 2FA code. Please try again.");
      } else {
        setError("Invalid username or password");
        setRequires2FA(false);
        setTotpCode("");
      }
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
            disabled={requires2FA}
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
            disabled={requires2FA}
          />
        </div>
        {requires2FA && (
          <div className={styles["form-group"]}>
            <label className={styles["form-label"]}>2FA Code:</label>
            <input
              type="text"
              value={totpCode}
              onChange={(e) => setTotpCode(e.target.value)}
              required
              className={styles["form-input"]}
              placeholder="Enter 6-digit code"
              maxLength={6}
            />
          </div>
        )}
        {error && <div className={styles["error-message"]}>{error}</div>}
        <button
          type="submit"
          disabled={loading}
          className={styles["submit-button"]}
        >
          {loading ? "Logging in..." : "Login"}
        </button>
        {requires2FA && (
          <button
            type="button"
            onClick={() => {
              setRequires2FA(false);
              setTotpCode("");
              setError("");
            }}
            className={styles["back-button"]}
          >
            Back
          </button>
        )}
      </form>
      <p className={styles["register-link"]}>
        Don't have an account? <Link to="/register">Register here</Link>
      </p>
    </div>
  );
};

export default LoginPage;
