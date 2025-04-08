// 4. Update the IQuestionController interface
package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
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
            String orderDirection,
            Integer page,
            Integer pageSize);

    ResponseEntity<Void> addQuestion(Question question);

    ResponseEntity<Void> deleteQuestion(Long questionId);

    ResponseEntity<Void> updateQuestion(Question question);

    ResponseEntity<Question> getQuestion(Long id);

    ResponseEntity<Resource> getIntroVideo();
}