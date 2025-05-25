package com.kamehoot.kamehoot_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.DTOs.AuthenticateRequest;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.models.Quiz;
import com.kamehoot.kamehoot_backend.models.QuizQuestion;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IQuizQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IQuizRepository;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IQuestionRepository questionRepository;
    private final IQuizRepository quizRepository;
    private final IQuizQuestionRepository quizQuestionRepository;

    public UserService(IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IQuestionRepository questionRepository, IQuizRepository quizRepository,
            IQuizQuestionRepository quizQuestionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;

    }

    @Override
    public void registerUser(AuthenticateRequest request) {
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of("USER"));
        userRepository.save(user);
    }

    @Override
    public AppUser getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
                });
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found with username: " + username);
                });
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    // @Override
    // public void deleteUser(UUID userId) {
    // AppUser user = userRepository.findById(userId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ""));
    // userRepository.delete(user);
    // }

    // @Override
    // public void updateUser(UUID userId, AuthenticateRequest request) {
    // AppUser user = userRepository.findById(userId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User
    // not found: " +
    // userId));
    // user.setUsername(request.username());
    // user.setPassword(passwordEncoder.encode(request.password()));
    // userRepository.save(user);
    // }

    @Override
    public List<Question> getUserQuestionList(UUID userId) {
        if (this.userRepository.existsById(userId) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }
        return this.questionRepository.findByCreatorId(userId);
    }

    @Override
    public void addUserQuestion(UUID id, Question question) {
        AppUser user = this.userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
                });

        question.setCreator(user);
        question.setCreationDate(LocalDateTime.now());

        try {
            questionRepository.save(question);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to save the question: " + e.getMessage());
        }
    }

    @Override
    public void deleteUserQuestion(UUID id, UUID questionId) {
        if (this.userRepository.existsById(id) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }

        try {
            this.questionRepository.deleteById(questionId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't delete question with id: " + questionId + " Error: " + e.getMessage());

        }
    }

    @Override
    public List<Quiz> getUserQuizList(UUID userId) {
        AppUser user = getUserById(userId);
        return user.getUserQuizzes();
    }

    @Override
    public void addUserQuiz(UUID id, Quiz quiz) {
        AppUser user = this.userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
                });

        quiz.setCreator(user);
        quiz.setCreationDate(LocalDateTime.now());
        Quiz savedQuiz = null;

        try {
            savedQuiz = this.quizRepository.save(quiz);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save quiz");

        }

        if (quiz.getQuizQuestions() != null && !quiz.getQuizQuestions().isEmpty()) {
            try {
                for (QuizQuestion quizQuestion : quiz.getQuizQuestions()) {
                    quizQuestion.setQuiz(savedQuiz);
                    this.quizQuestionRepository.save(quizQuestion);
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Failed to save questionQuestion: " + e.getMessage());
            }
        }

    }

    @Override
    public void deleteUserQuiz(UUID id, UUID quizId) {
        if (this.userRepository.existsById(id) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }

        try {
            this.quizRepository.deleteById(quizId);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't delete question with id: " + quizId + " Error: " + e.getMessage());

        }
    }

    // @Override
    // public List<Question> getUserQuestionList(UUID userId) {

    // AppUser user = this.userRepository.findById(userId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User
    // not found: " + userId));

    // return user.getUserQuestions().stream().map(userQuestion ->
    // userQuestion.getQuestion()).toList();
    // }

    // @Override
    // public void addUserQuestion(UUID userId, UUID questionId) {

    // AppUser user = this.userRepository.findById(userId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user
    // not found"));
    // Question question = this.questionRepository.findById(questionId)
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
    // "question not found"));

    // UserQuestion userQuestion = new UserQuestion();
    // userQuestion.setUser(user);
    // userQuestion.setQuestion(question);
    // try {
    // this.userQuestionRepository.save(userQuestion);
    // } catch (Exception e) {
    // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question couldn't
    // be saved to the user list");
    // }
    // }

}
