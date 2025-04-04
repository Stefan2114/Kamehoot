package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.services.IQuestionService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/questions")
public class QuestionController implements IQuestionController {

    private final IQuestionService questionService;

    @Autowired
    public QuestionController(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<Question>> getQuestions() {

        return ResponseEntity.ok(this.questionService.getQuestions());
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> addQuestion(@RequestBody QuestionDTO questionDTO) {

        System.out.println(questionDTO);

        Question question = new Question(0L, questionDTO.getCreationDate(), questionDTO.getQuestionText(),
                questionDTO.getCategory(), questionDTO.getCorrectAnswer(), questionDTO.getWrongAnswers(),
                questionDTO.getDifficulty());
        this.questionService.addQuestion(question);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Long questionId) {
        this.questionService.deleteQuestionById(questionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping
    public ResponseEntity<Void> updateQuestion(@RequestBody Question question) {

        this.questionService.updateQuestion(question);
        return ResponseEntity.noContent().build();
    }
}
