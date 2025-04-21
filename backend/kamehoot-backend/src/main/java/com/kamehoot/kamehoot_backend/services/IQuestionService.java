// 1. First, modify the IQuestionService interface to include pagination parameters
package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

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

        void addQuestion(Question question);

        void updateQuestion(Question question);

        void deleteQuestionById(UUID questionId);

        Question getQuestion(UUID id);

        FileSystemResource getIntroVideo();
}