package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Question;

public interface IUserService {

    void registerUser(AuthenticateRequest request);

    AppUser getUserById(UUID userId);

    List<AppUser> getAllUsers();

    void deleteUser(UUID userId);

    void updateUser(UUID userId, AuthenticateRequest request);

    List<Question> getUserQuestionList(UUID userId);

    void addUserQuestion(UUID id, UUID questionId);

}