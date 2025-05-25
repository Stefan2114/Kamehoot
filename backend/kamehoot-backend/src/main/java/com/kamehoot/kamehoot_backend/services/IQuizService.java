package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.DTOs.QuizRequest;
import com.kamehoot.kamehoot_backend.models.Quiz;

public interface IQuizService {

    List<Quiz> getUserQuizList(UUID userId);

    void addQuiz(UUID id, QuizRequest quizDTO);

    void deleteQuiz(UUID quizId);

}