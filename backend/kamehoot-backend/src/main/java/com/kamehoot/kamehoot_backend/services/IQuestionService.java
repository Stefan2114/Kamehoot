package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionService {
    List<Question> getQuestions(
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection);

    List<Question> getQuestions(); // Keep the original method for backward compatibility

    void addQuestion(Question question);

    void updateQuestion(Question question);

    void deleteQuestionById(Long questionId);

    Question getQuestion(Long id);

}