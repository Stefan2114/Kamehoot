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
import AddQuizPage from "./pages/AddQuizPage";
import HomePage from "./pages/HomePage";
import GamesPage from "./pages/GamesPage";
import CreateGamePage from "./pages/CreateGamePage";
import PlayGamePage from "./pages/PlayGamePage";

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
      { index: true, element: <Navigate to="/" replace /> },

      {
        path: "/",
        element: <HomePage />,
      },
      {
        path: "/home",
        element: <HomePage />,
      },
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
      {
        path: "/quizzes/add",
        element: (
          <ProtectedWrapper>
            <AddQuizPage />
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
      {
        path: "/quizzes/add",
        element: (
          <ProtectedWrapper>
            <AddQuizPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/games",
        element: (
          <ProtectedWrapper>
            <GamesPage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/games/create",
        element: (
          <ProtectedWrapper>
            <CreateGamePage />
          </ProtectedWrapper>
        ),
      },
      {
        path: "/games/:gameCode",
        element: (
          <ProtectedWrapper>
            <PlayGamePage />
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
