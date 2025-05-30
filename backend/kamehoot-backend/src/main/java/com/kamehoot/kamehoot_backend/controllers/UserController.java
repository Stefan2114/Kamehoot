package com.kamehoot.kamehoot_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.models.AppUser;
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

}
