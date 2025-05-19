package com.kamehoot.kamehoot_backend.services;

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
import com.kamehoot.kamehoot_backend.models.UserQuestion;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IUserQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserQuestionRepository userQuestionRepository;
    private final IQuestionRepository questionRepository;

    public UserService(IUserRepository userRepository, IUserQuestionRepository userQuestionRepository,
            PasswordEncoder passwordEncoder,
            IQuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userQuestionRepository = userQuestionRepository;
        this.questionRepository = questionRepository;
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
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                });
    }

    @Override
    public AppUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
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

    @Override
    public void deleteUser(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ""));
        userRepository.delete(user);
    }

    @Override
    public void updateUser(UUID userId, AuthenticateRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " +
                        userId));
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }
    // public String authenticateUser(LoginRequest request) {
    // AppUser user = userRepository.findByUsername(request.getUsername())
    // .orElseThrow(() -> new UsernameNotFoundException("User not found: " +
    // request.getUsername()));
    // if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    // throw new BadCredentialsException("Invalid password");
    // }
    // return jwtService.generateToken(user);
    // }
    // public String refreshToken(String token) {
    // String username = jwtService.extractUsername(token);
    // AppUser user = userRepository.findByUsername(username)
    // .orElseThrow(() -> new UsernameNotFoundException("User not found: " +
    // username));
    // if (jwtService.isTokenExpired(token)) {
    // throw new TokenExpiredException("Token expired");
    // }
    // return jwtService.generateToken(user);
    // }

    @Override
    public List<Question> getUserQuestionList(UUID userId) {

        AppUser user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        return user.getUserQuestions().stream().map(userQuestion -> userQuestion.getQuestion()).toList();
    }

    @Override
    public void addUserQuestion(UUID userId, UUID questionId) {

        AppUser user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found"));
        Question question = this.questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "question not found"));

        UserQuestion userQuestion = new UserQuestion();
        userQuestion.setUser(user);
        userQuestion.setQuestion(question);
        try {
            this.userQuestionRepository.save(userQuestion);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "question couldn't be saved to the user list");
        }
    }

}
