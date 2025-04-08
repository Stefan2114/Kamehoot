// 1. First, modify the IQuestionService interface to include pagination parameters
package com.kamehoot.kamehoot_backend.services;

import java.util.List;

import org.springframework.core.io.FileSystemResource;

import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionService {
    List<Question> getQuestions();

    List<Question> getQuestions(
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection);

    // Add new method with pagination support
    List<Question> getQuestionsPaginated(
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection,
            int page,
            int pageSize);

    void addQuestion(Question question);

    void updateQuestion(Question question);

    void deleteQuestionById(Long questionId);

    Question getQuestion(Long id);

    FileSystemResource getIntroVideo();
}