package com.kamehoot.kamehoot_backend.DTOs;

public record TwoFaSetupRequest(String qrCodeUrl, String secretKey) {
}