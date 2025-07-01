package com.kamehoot.kamehoot_backend.DTOs;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuizRequest(@NotBlank String title, @NotBlank String description, @NotNull ZonedDateTime creationDate,
        @NotNull @Size(min = 1) List<UUID> questionIds) {
}
