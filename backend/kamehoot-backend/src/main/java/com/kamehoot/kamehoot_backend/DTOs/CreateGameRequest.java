package com.kamehoot.kamehoot_backend.DTOs;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequest(
        @NotNull UUID quizId,
        @Min(10) @Max(120) Integer questionTimeLimit) {

}
