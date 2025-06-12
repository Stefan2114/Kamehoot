package com.kamehoot.kamehoot_backend.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TwoFaVerifyRequest(
        @NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username,

        @NotBlank(message = "Password cannot be blank") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String password,

        @Min(value = 100000, message = "TOTP code must be 6 digits") @Max(value = 999999, message = "TOTP code must be 6 digits") int totpCode) {
}