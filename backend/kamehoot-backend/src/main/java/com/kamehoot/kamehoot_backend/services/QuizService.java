package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.DTOs.QuizRequest;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.models.Quiz;
import com.kamehoot.kamehoot_backend.models.QuizQuestion;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IQuizQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IQuizRepository;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;

@Service
public class QuizService implements IQuizService {
    private final IUserRepository userRepository;
    private final IQuestionRepository questionRepository;
    private final IQuizRepository quizRepository;
    private final IQuizQuestionRepository quizQuestionRepository;

    public QuizService(IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IQuestionRepository questionRepository, IQuizRepository quizRepository,
            IQuizQuestionRepository quizQuestionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;

    }

    @Override
    public List<Quiz> getUserQuizzes(String username) {

        return quizRepository.findByCreatorUsername(username);
    }

    @Override
    public List<Quiz> getQuizzes() {
        return this.quizRepository.findAllQuizzes();
    }

    @Override
    public void addQuiz(String username, QuizRequest quizDTO) {

        AppUser user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found with username: " + username);
                });

        Quiz quiz = new Quiz();
        quiz.setCreator(user);
        quiz.setCreationDate(quizDTO.creationDate());
        quiz.setTitle(quizDTO.title());
        quiz.setDescription(quizDTO.description());
        Quiz savedQuiz = this.quizRepository.save(quiz);

        try {
            for (UUID questionId : quizDTO.questionIds()) {
                Question question = this.questionRepository.findById(questionId).orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Question not found with id: " + questionId);
                });

                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setQuiz(savedQuiz);
                quizQuestion.setQuestion(question);
                ////////////////////////////////////
                this.quizQuestionRepository.save(quizQuestion);
            }
        } catch (Exception e) {
            this.quizRepository.deleteById(savedQuiz.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed to save questionQuestion: " + e.getMessage());
        }

    }

    @Override
    public Quiz getQuiz(UUID id) {

        return this.quizRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        });

    }

    @Override
    public void deleteQuiz(UUID id) {
        try {
            // this.quizRepository.deleteById(id);
            Quiz quiz = this.quizRepository.findById(id).get();
            quiz.setDeleted(true);
            this.quizRepository.save(quiz);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't delete quiz with id: " + id + " Error: " + e.getMessage());

        }
    }

}
