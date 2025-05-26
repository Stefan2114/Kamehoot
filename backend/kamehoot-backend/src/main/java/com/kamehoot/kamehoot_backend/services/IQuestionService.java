package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionService {

    List<Question> getAllQuestions();

    List<Question> getPublicQuestions();

    List<Question> getPrivateQuestions(UUID userId);

    Question getQuestion(UUID id);

    // should be changed to use db and pagination
    List<Question> getQuestions(UUID userId,
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection);

    void updateQuestion(QuestionDTO questionDTO);

    void addUserQuestion(String username, QuestionDTO questionDTO);

    void deleteQuestionById(UUID questionId);

}