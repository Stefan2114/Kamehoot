import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import QuestionsPage from "./pages/QuestionsPage";
import AddQuestionPage from "./pages/AddQuestionPage";
import EditQuestionPage from "./pages/EditQuestionPage";
import QuestionPage from "./pages/QuestionPage";

const router = createBrowserRouter([
  {
    path: "/questions",
    element: <QuestionsPage />,
  },
  {
    path: "/questions/add",
    element: <AddQuestionPage />,
  },
  {
    path: "/questions/edit/:id",
    element: <EditQuestionPage />,
  },
  {
    path: "/questions/:id",
    element: <QuestionPage />,
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
