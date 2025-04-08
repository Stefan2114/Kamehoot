// src/main.tsx

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { OfflineProvider } from "./contexts/OfflineContext";
import Layout from "./components/Layout";
import QuestionsPage from "./pages/QuestionsPage";
import AddQuestionPage from "./pages/AddQuestionPage";
import EditQuestionPage from "./pages/EditQuestionPage";
import QuestionPage from "./pages/QuestionPage";
import * as serviceWorkerRegistration from "./serviceWorkerRegistration";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        path: "questions",
        element: <QuestionsPage />,
      },
      {
        path: "questions/add",
        element: <AddQuestionPage />,
      },
      {
        path: "questions/edit/:id",
        element: <EditQuestionPage />,
      },
      {
        path: "questions/:id",
        element: <QuestionPage />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <OfflineProvider>
      <RouterProvider router={router} />
    </OfflineProvider>
  </StrictMode>
);

// Register the service worker for offline support
serviceWorkerRegistration.register();
