package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionDTO {

    private Long id;
    private LocalDateTime creationDate;

    private String questionText;

    private String category;

    private String correctAnswer;

    private List<String> wrongAnswers;

    private Integer difficulty;

}
