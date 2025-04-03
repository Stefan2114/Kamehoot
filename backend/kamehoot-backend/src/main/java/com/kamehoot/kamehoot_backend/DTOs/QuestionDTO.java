package com.kamehoot.kamehoot_backend.DTOs;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class QuestionDTO {

    private Date creationDate;

    private String questionText;

    private String category;

    private String correctAnswer;

    private List<String> wrongAnswers;

    private Integer difficulty;

}