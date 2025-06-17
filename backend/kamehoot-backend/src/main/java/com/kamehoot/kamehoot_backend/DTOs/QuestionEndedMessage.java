package com.kamehoot.kamehoot_backend.DTOs;

public record QuestionEndedMessage(

        String username,
        Boolean isCorrect,
        Long responseTime) {

}
