import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
  Outlet,
} from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import { ProtectedRoute } from "./components/ProtectedRoute";
import QuestionsPage from "./pages/QuestionsPage";
import AddQuestionPage from "./pages/AddQuestionPage";
import EditQuestionPage from "./pages/EditQuestionPage";
import QuestionPage from "./pages/QuestionPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ProfilePage from "./pages/ProfilePage";
import Layout from "./components/Layout";
import QuizzesPage from "./pages/QuizzesPage";
import QuizPage from "./pages/QuizPage";

const AppLayout = () => {
  return (
    <Layout>
      <Outlet />
    </Layout>
  );
};

const ProtectedWrapper = ({ children }: { children: React.ReactNode }) => {
  return <ProtectedRoute>{children}</ProtectedRoute>;
};

const router = createBrowserRouter([
  {
    path: "/",
    element: <AppLayout />,
    children: [
      { index: true, element: <Navigate to="/questions" replace /> },

      {
        path: "/login",
        element: <LoginPage />,
      },
      {
        path: "/register",
        element: <RegisterPage />,
      },
      {
        path: "/profile",
        element: (
          <ProtectedWrapper>
            <ProfilePage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/questions",
        element: (
          <ProtectedWrapper>
            <QuestionsPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/questions/add",
        element: (
          <ProtectedWrapper>
            <AddQuestionPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/questions/edit/:id",
        element: (
          <ProtectedWrapper>
            <EditQuestionPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/questions/:id",
        element: (
          <ProtectedWrapper>
            <QuestionPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/quizzes",
        element: (
          <ProtectedWrapper>
            <QuizzesPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/quizzes/:id",
        element: (
          <ProtectedWrapper>
            <QuizPage />
          </ProtectedWrapper>
        ),
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </StrictMode>
);
