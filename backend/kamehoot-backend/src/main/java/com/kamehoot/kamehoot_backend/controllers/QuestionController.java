// 3. Update the QuestionController
package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.services.IQuestionService;
import com.kamehoot.kamehoot_backend.services.IUserService;

@RestController
@RequestMapping("/questions")
public class QuestionController implements IQuestionController {
    private final IQuestionService questionService;
    private final IUserService userService;

    @Autowired
    public QuestionController(IQuestionService questionService, IUserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<Question>> getQuestions(
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<Integer> difficulties,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "creationDate") String orderBy,
            @RequestParam(required = false, defaultValue = "desc") String orderDirection) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;

        // Get authenticated user ID if available
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();
            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                System.out.println("User not found: " + username);
            }
        }

        // Check if any filter parameters are provided
        boolean hasFilters = (categories != null && !categories.isEmpty()) ||
                (difficulties != null && !difficulties.isEmpty()) ||
                (searchTerm != null && !searchTerm.isEmpty()) ||
                !orderBy.equals("creationDate") ||
                !orderDirection.equals("desc");

        if (hasFilters || authenticatedUserId != null) {
            // Use the filtered endpoint with authentication context
            return ResponseEntity.ok(
                    this.questionService.getQuestions(
                            authenticatedUserId,
                            categories,
                            difficulties,
                            searchTerm,
                            orderBy,
                            orderDirection));
        } else {
            // Fallback for unauthenticated users with no filters (public only)
            return ResponseEntity.ok(this.questionService.getPublicQuestions());
        }
    }

    @Override
    @GetMapping("/public")
    public ResponseEntity<List<Question>> getPublicQuestions() {

        return ResponseEntity.ok(
                this.questionService.getPublicQuestions());

    }

    @Override
    @GetMapping("/private")
    public ResponseEntity<List<Question>> getPrivateQuestions() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                System.out.println("User not found: " + username);
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(this.questionService.getPrivateQuestions(authenticatedUserId));

    }

    @Override
    @PostMapping
    public ResponseEntity<Void> addQuestion(@RequestBody QuestionDTO question) {
        System.out.println(question);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            this.questionService.addUserQuestion(username, question);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") UUID questionId) {
        System.out.println("I deleted the question with id: " + questionId);
        this.questionService.deleteQuestionById(questionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping
    public ResponseEntity<Void> updateQuestion(@RequestBody QuestionDTO question) {
        System.out.println(question);
        this.questionService.updateQuestion(question);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable UUID id) {
        System.out.println(id);
        System.out.println("I got here to get a question by id");
        return ResponseEntity.ok(this.questionService.getQuestion(id));
    }

}