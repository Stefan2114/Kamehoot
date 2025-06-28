import TopBar from "./TopBar";
import styles from "../styles/Layout.module.css";

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className={styles["layout-container"]}>
      <TopBar />
      <main className={styles["layout-main"]}>
        <div className={styles["layout-content"]}>{children}</div>
      </main>
    </div>
  );
};

export default Layout;
