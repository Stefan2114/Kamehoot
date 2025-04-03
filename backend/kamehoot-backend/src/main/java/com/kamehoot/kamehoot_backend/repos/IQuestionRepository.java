package com.kamehoot.kamehoot_backend.repos;

import java.util.List;

import com.kamehoot.kamehoot_backend.models.Question;

public interface IQuestionRepository {

    public Question add(Question question);

    public Question update(Question question);

    public void deleteById(Long questionId);

    public List<Question> findAll();

}
