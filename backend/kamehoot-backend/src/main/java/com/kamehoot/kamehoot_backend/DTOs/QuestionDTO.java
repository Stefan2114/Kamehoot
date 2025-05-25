package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionDTO(UUID id, @NotNull LocalDateTime creationDate, @NotBlank String questionText,
        @NotBlank String category,
        @NotBlank String correctAnswer, @NotNull @Size(min = 1) List<String> wrongAnswers,
        @NotNull Integer difficulty) {

}
