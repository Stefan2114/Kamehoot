package com.kamehoot.kamehoot_backend.DTOs;

import java.util.List;
import java.util.UUID;

public record QuestionResultDTO(UUID questionId,
        String questionText,
        String correctAnswer,
        List<PlayerAnswerDTO> playerAnswers,
        List<PlayerDTO> leaderboard) {

}
