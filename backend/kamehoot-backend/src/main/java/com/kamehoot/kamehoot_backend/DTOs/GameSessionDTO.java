package com.kamehoot.kamehoot_backend.DTOs;

import java.util.UUID;

public record GameSessionDTO(

        UUID id,
        String quizTitle,
        Integer totalQuestions) {

}
