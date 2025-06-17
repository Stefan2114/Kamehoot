package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.DTOs.QuizRequest;
import com.kamehoot.kamehoot_backend.models.Quiz;

public interface IQuizService {

    List<Quiz> getUserQuizzes(String username);

    List<Quiz> getQuizzes();

    void addQuiz(String userId, QuizRequest quizDTO);

    Quiz getQuiz(UUID id);

    void deleteQuiz(UUID id);

}