package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void registerUser(AuthenticateRequest request) {

        if (existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of("USER"));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to register user: " + e.getMessage());
        }
    }

    @Override
    public AppUser getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
                });
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found with username: " + username);
                });
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void setTwoFaSecret(String username, String secret) {

        if (secret == null || secret.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Two-factor authentication secret cannot be empty");
        }
        AppUser user = getUserByUsername(username);
        user.setTwoFaSecret(secret);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void enable2FA(String username) {
        AppUser user = getUserByUsername(username);
        user.setTwoFaEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void disable2FA(String username) {
        AppUser user = getUserByUsername(username);
        user.setTwoFaEnabled(false);
        user.setTwoFaSecret(null);
        userRepository.save(user);
    }

    // @Override
    // public void deleteUser(UUID userId) {
    // AppUser user = userRepository.findById(userId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ""));
    // userRepository.delete(user);
    // }

    // @Override
    // public void updateUser(UUID userId, AuthenticateRequest request) {
    // AppUser user = userRepository.findById(userId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User
    // not found: " +
    // userId));
    // user.setUsername(request.username());
    // user.setPassword(passwordEncoder.encode(request.password()));
    // userRepository.save(user);
    // }

}
