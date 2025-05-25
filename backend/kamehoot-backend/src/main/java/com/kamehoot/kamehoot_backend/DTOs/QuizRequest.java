package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuizRequest(@NotBlank String title, @NotNull LocalDateTime creationDate,
        @NotNull @Size(min = 1) List<UUID> questionIds) {
}
