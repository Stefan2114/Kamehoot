package com.kamehoot.kamehoot_backend.DTOs;

public record TokenResponse(String token, Long expirationSeconds, Boolean requires2FA, String message) {
    // Constructor for regular responses
    public TokenResponse(String token, Long expirationSeconds) {
        this(token, expirationSeconds, false, null);
    }
}
