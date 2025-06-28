package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GameQuestionDTO(

                UUID questionId,
                String questionText,
                List<String> options,
                Integer questionNumber,
                Integer timeLimit) {

}
