package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuestionDTO(UUID id, LocalDateTime creationDate, String questionText, String category,
                String correctAnswer, List<String> wrongAnswers, Integer difficulty) {

}
