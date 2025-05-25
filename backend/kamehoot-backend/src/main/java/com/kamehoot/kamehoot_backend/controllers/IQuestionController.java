// 4. Update the IQuestionController interface
package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionController {

    ResponseEntity<List<Question>> getQuestions(
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection);

    ResponseEntity<Void> addQuestion(QuestionDTO questionDTO); // i need to see how can
    // i get the User from the token

    ResponseEntity<Void> deleteQuestion(UUID questionId); // i need to make sure the
    // one that deletes it is either admin or the one who created it

    ResponseEntity<Void> updateQuestion(QuestionDTO questionDTO); // the same here

    ResponseEntity<Question> getQuestion(UUID id);

}