package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.DTOs.QuestionIdDTO;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IUserController {

    ResponseEntity<List<AppUser>> getAllUsers();

    // ResponseEntity<Void> deleteUserById(UUID id); // i need to see how i can do
    // that using the info from token

    // ResponseEntity<Void> updateUserPassword(UUID id, AuthenticateRequest
    // request); i need to see how can i return back a new token

    // ResponseEntity<List<Question>> getUserQuestionList(UUID id); i think i will
    // create a quiz entity

    // ResponseEntity<Void> addUserQuestion(UUID id, QuestionIdDTO questionId); //
    // the same here

}