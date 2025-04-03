package com.kamehoot.kamehoot_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;

@Service
public class QuestionService implements IQuestionService {

        private final IQuestionRepository questionRepository;

        @Autowired
        public QuestionService(IQuestionRepository questionRepository) {
                this.questionRepository = questionRepository;
        }

        @Override
        public List<Question> getQuestions() {

                return this.questionRepository.findAll();
        }

        @Override
        public void addQuestion(Question question) {
                try {
                        this.questionRepository.add(question);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
        }

        @Override
        public void updateQuestion(Question question) {

                if (question.getId() == 0L) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "For update the id should not be null");
                }
                try {
                        this.questionRepository.update(question);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

        @Override
        public void deleteQuestionById(Long questionId) {
                try {
                        this.questionRepository.deleteById(questionId);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

}
