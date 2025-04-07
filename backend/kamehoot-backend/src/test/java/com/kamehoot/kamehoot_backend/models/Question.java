package com.kamehoot.kamehoot_backend.models;

import java.util.Date;
import java.util.List;

public class Question {

    private Long id;

    private Date creationDate;

    private String questionText;

    private String category;

    private String correctAnswer;

    private List<String> wrongAnswers;

    private Integer difficulty;

    public Question(Long id, Date creationDate, String questionText, String category, String correctAnswer,
            List<String> wrongAnswers, Integer difficulty) {
        this.id = id;
        this.creationDate = creationDate;
        this.questionText = questionText;
        this.category = category;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers = wrongAnswers;
        this.difficulty = difficulty;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Question)) {
            return false;
        }

        return this.id == ((Question) other).id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(List<String> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

}
