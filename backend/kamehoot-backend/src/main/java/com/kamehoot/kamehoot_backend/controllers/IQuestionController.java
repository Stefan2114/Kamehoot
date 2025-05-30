package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionController {

    ResponseEntity<List<QuestionDTO>> getQuestions(
            List<String> categories,
            List<Integer> difficulties,
            String searchTerm,
            String orderBy,
            String orderDirection);

    ResponseEntity<List<Question>> getPublicQuestions();

    ResponseEntity<List<QuestionDTO>> getPrivateQuestions();

    ResponseEntity<Void> addQuestion(QuestionDTO question);

    ResponseEntity<Void> deleteQuestion(UUID questionId);

    ResponseEntity<Void> updateQuestion(QuestionDTO question);

    ResponseEntity<QuestionDTO> getQuestion(UUID id);

}