package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.kamehoot.kamehoot_backend.enums.GameStatus;

public record GameSessionDTO(

        UUID id,
        String gameCode,
        GameStatus status,
        String quizTitle,
        Integer currentQuestionIndex,
        Integer totalQuestions,
        List<PlayerDTO> players,
        LocalDateTime createdAt) {

}
