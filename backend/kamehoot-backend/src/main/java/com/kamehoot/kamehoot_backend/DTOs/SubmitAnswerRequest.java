package com.kamehoot.kamehoot_backend.DTOs;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(

                @NotNull UUID gameSessionId,
                @NotNull UUID questionId,
                @NotNull String answer,
                @NotNull ZonedDateTime answerTime) {

}
