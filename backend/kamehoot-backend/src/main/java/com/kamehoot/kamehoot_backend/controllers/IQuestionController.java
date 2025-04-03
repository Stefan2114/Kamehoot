package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionController {

    ResponseEntity<List<Question>> getQuestions();

    ResponseEntity<Void> addQuestion(Question question);

    ResponseEntity<Void> deleteQuestion(Long questionId);

    ResponseEntity<Void> updateQuestion(Question question);

    ResponseEntity<Question> getQuestion(Long id);
}
