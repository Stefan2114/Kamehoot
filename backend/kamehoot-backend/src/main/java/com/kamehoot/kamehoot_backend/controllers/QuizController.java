package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.DTOs.QuizDTO;
import com.kamehoot.kamehoot_backend.DTOs.QuizRequest;
import com.kamehoot.kamehoot_backend.models.Quiz;
import com.kamehoot.kamehoot_backend.services.IQuizService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final IQuizService quizService;

    public QuizController(IQuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<Void> addQuiz(@Valid @RequestBody QuizRequest quiz) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            this.quizService.addQuiz(username, quiz);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Quiz>> getQuizzes() {

        return ResponseEntity.ok(this.quizService.getQuizzes());
    }

    @GetMapping("/private")
    public ResponseEntity<List<QuizDTO>> getPrivateQuizzes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            List<Quiz> quizzes = this.quizService.getUserQuizzes(username);
            return ResponseEntity.ok(mapQuizzesToDTOs(quizzes));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuiz(@NotNull @PathVariable UUID id) {
        return ResponseEntity.ok(mapQuizToDTO(this.quizService.getQuiz(id)));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@NotNull @PathVariable UUID id) {
        this.quizService.deleteQuiz(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private QuizDTO mapQuizToDTO(Quiz quiz) {
        return new QuizDTO(quiz.getId(), quiz.getDeleted(),
                quiz.getTitle(), quiz.getDescription(), quiz.getCreationDate(),
                quiz.getQuestions().stream().map(quizQuestion -> quizQuestion.getQuestion()).toList());
    }

    private List<QuizDTO> mapQuizzesToDTOs(List<Quiz> quizzes) {
        return quizzes.stream().map(quiz -> mapQuizToDTO(quiz)).toList();
    }

}
