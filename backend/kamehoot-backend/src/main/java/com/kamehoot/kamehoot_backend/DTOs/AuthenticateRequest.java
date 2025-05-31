package com.kamehoot.kamehoot_backend.DTOs;

import jakarta.validation.constraints.NotBlank;

public record AuthenticateRequest(@NotBlank String username, @NotBlank String password, Integer totpCode) {

}
