package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.DTOs.QuestionIdDTO;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IUserController {

    ResponseEntity<Void> registerUser(AuthenticateRequest request);

    ResponseEntity<List<AppUser>> getAllUsers();

    ResponseEntity<Void> deleteUserById(UUID id);

    ResponseEntity<Void> updateUserPassword(UUID id, AuthenticateRequest request);

    ResponseEntity<List<Question>> getUserQuestionList(UUID id);

    ResponseEntity<Void> addUserQuestion(UUID id, QuestionIdDTO questionId);

}