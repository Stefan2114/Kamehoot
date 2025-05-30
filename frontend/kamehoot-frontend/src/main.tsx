import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
} from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import { ProtectedRoute } from "./components/ProtectedRoute";
import QuestionsPage from "./pages/QuestionsPage";
import AddQuestionPage from "./pages/AddQuestionPage";
import EditQuestionPage from "./pages/EditQuestionPage";
import QuestionPage from "./pages/QuestionPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Navigate to="/questions" replace />,
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
    path: "/questions",
    element: (
      <ProtectedRoute>
        <QuestionsPage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/questions/add",
    element: (
      <ProtectedRoute>
        <AddQuestionPage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/questions/edit/:id",
    element: (
      <ProtectedRoute>
        <EditQuestionPage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/questions/:id",
    element: (
      <ProtectedRoute>
        <QuestionPage />
      </ProtectedRoute>
    ),
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </StrictMode>
);
