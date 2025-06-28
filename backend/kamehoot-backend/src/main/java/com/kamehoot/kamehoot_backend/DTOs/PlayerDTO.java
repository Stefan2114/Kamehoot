package com.kamehoot.kamehoot_backend.DTOs;

public record PlayerDTO(String username,
                Integer totalScore,
                Boolean hasAnswered // for current question
) {

}
