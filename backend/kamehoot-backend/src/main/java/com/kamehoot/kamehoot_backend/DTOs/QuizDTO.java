package com.kamehoot.kamehoot_backend.DTOs;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.models.Question;

public record QuizDTO(UUID id, Boolean deleted, String title, String description, ZonedDateTime creationDate,
        // Integer maxPossibleScore,
        List<Question> questions) {
}