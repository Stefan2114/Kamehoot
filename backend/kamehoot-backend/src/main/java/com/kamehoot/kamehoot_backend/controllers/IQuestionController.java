package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionController {

    ResponseEntity<List<Question>> getQuestions();

    ResponseEntity<Void> addQuestion(QuestionDTO questionDTO);

    ResponseEntity<Void> deleteQuestion(Long questionId);

    ResponseEntity<Void> updateQuestion(Question question);
}
