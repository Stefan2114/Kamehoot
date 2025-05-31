package com.kamehoot.kamehoot_backend.DTOs;

public record TwoFaVerifyRequest(String username, String password, int totpCode) {
}