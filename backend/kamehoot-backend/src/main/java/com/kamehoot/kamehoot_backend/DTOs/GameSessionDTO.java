package com.kamehoot.kamehoot_backend.DTOs;

import java.util.UUID;

import com.kamehoot.kamehoot_backend.enums.GameStatus;

public record GameSessionDTO(

                UUID id,
                GameStatus status,
                String quizTitle,
                Integer currentQuestionIndex,
                Integer totalQuestions) {

}
