package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.DTOs.QuestionIdDTO;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.services.IUserService;

@RestController
@RequestMapping("/users")
public class UserController implements IUserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    // @Override
    // @PostMapping("/register")
    // public ResponseEntity<Void> registerUser(@RequestBody AuthenticateRequest
    // request) {
    // this.userService.registerUser(request);
    // return ResponseEntity.noContent().build();
    // }

    // @Override
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
    // this.userService.deleteUser(id);
    // return ResponseEntity.noContent().build();
    // }

    // @Override
    // @PutMapping("/{id}")
    // public ResponseEntity<Void> updateUserPassword(@PathVariable UUID id,
    // @RequestBody AuthenticateRequest request) {
    // this.userService.updateUser(id, request);
    // return ResponseEntity.noContent().build();
    // }

    // @Override
    // @GetMapping("/{id}/questions")
    // public ResponseEntity<List<Question>> getUserQuestionList(@PathVariable UUID
    // id) {
    // return ResponseEntity.ok(this.userService.getUserQuestionList(id));
    // }

    // @Override
    // @PostMapping("/{id}/questions")
    // public ResponseEntity<Void> addUserQuestion(@PathVariable UUID id,
    // @RequestBody QuestionIdDTO questionId) {

    // this.userService.addUserQuestion(id, questionId.id());
    // return ResponseEntity.noContent().build();
    // }
}
