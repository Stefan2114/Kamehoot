package com.kamehoot.kamehoot_backend.models;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Question {

    private Long id;

    private Date creationDate;

    private String questionText;

    private String category;

    private String correctAnswer;

    private List<String> wrongAnswers;

    private Integer difficulty;

}
