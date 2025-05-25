package com.kamehoot.kamehoot_backend.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public List<Quiz> getUserQuizList(UUID userId) {
        return quizRepository.findByCreatorId(userId);
    }

    @Override
    public void addQuiz(UUID userId, QuizRequest quizDTO) {
        AppUser user = this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
                });

        Quiz quiz = new Quiz();
        quiz.setCreator(user);
        quiz.setCreationDate(quizDTO.creationDate());
        quiz.setTitle(quizDTO.title());
        Quiz savedQuiz = this.quizRepository.save(quiz);

        if (quizDTO.questionIds() != null) {
            try {
                for (UUID questionId : quizDTO.questionIds()) {
                    Question question = this.questionRepository.findById(questionId).orElseThrow(() -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Question not found with id: " + questionId);
                    });
                    QuizQuestion quizQuestion = new QuizQuestion();
                    quizQuestion.setQuiz(savedQuiz);
                    quizQuestion.setQuestion(question);
                    this.quizQuestionRepository.save(quizQuestion);
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Failed to save questionQuestion: " + e.getMessage());
            }
        }

    }

    @Override
    public void deleteQuiz(UUID quizId) {
        try {
            this.quizRepository.deleteById(quizId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't delete question with id: " + quizId + " Error: " + e.getMessage());

        }
    }
}
