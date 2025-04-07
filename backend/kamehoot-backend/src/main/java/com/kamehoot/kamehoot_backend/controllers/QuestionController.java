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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @Override
    @GetMapping
    public ResponseEntity<List<Question>> getQuestions(
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<Integer> difficulties,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "id") String orderBy,
            @RequestParam(required = false, defaultValue = "asc") String orderDirection) {

        // Check if any filter parameters are provided
        boolean hasFilters = (categories != null && !categories.isEmpty()) ||
                (difficulties != null && !difficulties.isEmpty()) ||
                (searchTerm != null && !searchTerm.isEmpty()) ||
                !orderBy.equals("id") ||
                !orderDirection.equals("asc");

        if (hasFilters) {
            // If filters are provided, use the new paginated endpoint
            return ResponseEntity.ok(
                    this.questionService.getQuestions(
                            categories,
                            difficulties,
                            searchTerm,
                            orderBy,
                            orderDirection));
        } else {
            // Otherwise fall back to the original endpoint for backward compatibility
            return ResponseEntity.ok(this.questionService.getQuestions());
        }
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> addQuestion(@RequestBody Question question) {
        System.out.println(question);
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
        System.out.println(question);
        this.questionService.updateQuestion(question);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(this.questionService.getQuestion(id));
    }

}