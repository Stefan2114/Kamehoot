package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.models.AppUser;

public interface IUserService {

    void registerUser(AuthenticateRequest request);

    AppUser getUserById(UUID userId);

    AppUser getUserByUsername(String username);

    Boolean existsByUsername(String username);

    List<AppUser> getAllUsers();

    void setTwoFaSecret(String username, String secret);

    void enable2FA(String username);

    void disable2FA(String username);

    // void deleteUser(UUID userId);

    // void updateUser(UUID userId, AuthenticateRequest request);

}