package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kamehoot.kamehoot_backend.models.AppUser;

public interface IUserController {

    ResponseEntity<List<AppUser>> getAllUsers();

    // ResponseEntity<Void> deleteUserById(UUID id); // i need to see how i can do
    // that using the info from token

    // ResponseEntity<Void> updateUserPassword(UUID id, AuthenticateRequest
    // request); i need to see how can i return back a new token

}