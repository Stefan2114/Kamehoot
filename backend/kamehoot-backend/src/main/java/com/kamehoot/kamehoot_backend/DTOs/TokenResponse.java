package com.kamehoot.kamehoot_backend.DTOs;

public record TokenResponse(String token, long expiresInSeconds) {

}
