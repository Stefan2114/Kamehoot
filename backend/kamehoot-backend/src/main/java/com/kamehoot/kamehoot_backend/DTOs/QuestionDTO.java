package com.kamehoot.kamehoot_backend.DTOs;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record QuestionDTO(UUID id,
                @NotNull(message = "Creation date cannot be null") @PastOrPresent(message = "Creation date cannot be in the future") ZonedDateTime creationDate,

                @NotBlank(message = "Question text cannot be blank") @Size(min = 5, max = 500, message = "Question text must be between 5 and 500 characters") String questionText,
                @NotBlank(message = "Category text cannot be blank") @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters") String category,

                @NotBlank(message = "Correct answer text cannot be blank") @Size(min = 1, max = 128, message = "Correct answer must be between 1 and 128 characters") String correctAnswer,

                @NotNull(message = "Wrong answers cannot be null") @Size(min = 1, max = 4, message = "Must have between 1 and 6 wrong answers") List<@NotBlank(message = "Wrong answer cannot be blank") @Size(min = 1, max = 128, message = "Wrong answer must be between 1 and 128 characters") String> wrongAnswers,
                @NotNull(message = "Difficulty cannot be null") @Min(value = 1, message = "Difficulty must be alteast 1") @Max(value = 3, message = "Difficulty must be at most 3") Integer difficulty) {

}
