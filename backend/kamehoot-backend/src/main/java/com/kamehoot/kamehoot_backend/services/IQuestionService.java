package com.kamehoot.kamehoot_backend.services;

import java.util.List;

import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionService {

    List<Question> getQuestions();

    void addQuestion(Question question);

    void updateQuestion(Question question);

    void deleteQuestionById(Long questionId);

    Question getQuestion(Long id);

}
