package com.kamehoot.kamehoot_backend.DTOs;

public record PlayerAnswerDTO(
        String username,
        String answer,
        Boolean isCorrect,
        Long responseTime,
        Integer pointsEarned) {

}
