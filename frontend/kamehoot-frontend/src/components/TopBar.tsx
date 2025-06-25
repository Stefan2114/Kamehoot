import { Link, useLocation, useNavigate } from "react-router-dom";
import styles from "../styles/TopBar.module.css";
import { useAuth } from "../contexts/AuthContext";

const TopBar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleRegister = () => {
    navigate("/register");
  };

  const handleSignIn = () => {
    navigate("/login");
  };

  return (
    <nav className={styles["topbar"]}>
      <div className={styles["topbar-nav"]}>
        <Link
          to="/home"
          className={`${styles["topbar-link"]} ${
            location.pathname === "/home" ? "active" : ""
          }`}
        >
          Home
        </Link>
        <Link
          to="/quizzes"
          className={`${styles["topbar-link"]} ${
            location.pathname === "/home" ? "active" : ""
          }`}
        >
          Quizzes
        </Link>
        <Link
          to="/questions"
          className={`${styles["topbar-link"]} ${
            location.pathname === "/home" ? "active" : ""
          }`}
        >
          Questions
        </Link>

        <Link to="/games">Games</Link>
        <Link
          to="/profile"
          className={`${styles["topbar-link"]} ${
            location.pathname === "/home" ? "active" : ""
          }`}
        >
          Profile
        </Link>

        <div className={styles["topbar-auth"]}>
          {isAuthenticated ? (
            <div className={styles["auth-section"]}>
              <span className={styles["user-name"]}>Welcome, {""}</span>
              <button
                onClick={handleLogout}
                className={styles["logout-button"]}
              >
                Logout
              </button>
            </div>
          ) : (
            <div className={styles["auth-section"]}>
              <button
                onClick={handleSignIn}
                className={styles["signin-button"]}
              >
                Sign In
              </button>
              <button
                onClick={handleRegister}
                className={styles["register-button"]}
              >
                Register
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};
export default TopBar;
