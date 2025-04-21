// 4. Update the IQuestionController interface
package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import com.kamehoot.kamehoot_backend.models.Question;
import org.springframework.core.io.Resource;

public interface IQuestionController {
    // Updated method signature to include pagination parameters
    ResponseEntity<List<Question>> getQuestions(
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection);

    ResponseEntity<Void> addQuestion(Question question);

    ResponseEntity<Void> deleteQuestion(UUID questionId);

    ResponseEntity<Void> updateQuestion(Question question);

    ResponseEntity<Question> getQuestion(UUID id);

    ResponseEntity<Resource> getIntroVideo();
}