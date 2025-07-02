package com.kamehoot.kamehoot_backend.DTOs;

import java.util.UUID;

import org.springframework.boot.context.properties.bind.DefaultValue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequest(
                @NotNull UUID quizId,
                @Min(5) @Max(60) Integer questionTimeLimit) {

}
