package com.kamehoot.kamehoot_backend.models;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Question {

    private Long id;

    private Date creationDate;

    private String questionText;

    private String category;

    private String correctAnswer;

    private List<String> wrongAnswers;

    private Integer difficulty;

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Question)) {
            return false;
        }

        return this.id == ((Question) other).id;
    }

}
