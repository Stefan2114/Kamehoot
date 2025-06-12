package com.kamehoot.kamehoot_backend.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthenticateRequest(

        @NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can contain only letters, numbers, and underscores") String username,

        @NotBlank(message = "Password cannot be blank") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$", message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character") String password,

        @Min(value = 100000, message = "TOTP code must be 6 digits") @Max(value = 999999, message = "TOTP code must be 6 digits") Integer totpCode) {

}
