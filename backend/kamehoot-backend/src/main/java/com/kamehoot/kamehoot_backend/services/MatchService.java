// package com.kamehoot.kamehoot_backend.services;

// import java.util.UUID;

// import org.springframework.http.HttpStatus;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.server.ResponseStatusException;

// import com.kamehoot.kamehoot_backend.models.AppUser;
// import com.kamehoot.kamehoot_backend.models.Quiz;
// import com.kamehoot.kamehoot_backend.models.UserMatch;
// import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;
// import com.kamehoot.kamehoot_backend.repos.IQuizQuestionRepository;
// import com.kamehoot.kamehoot_backend.repos.IQuizRepository;
// import com.kamehoot.kamehoot_backend.repos.IUserRepository;

// public class MatchService {

// private final IUserRepository userRepository;
// private final IQuestionRepository questionRepository;
// private final IQuizRepository quizRepository;
// private final IQuizQuestionRepository quizQuestionRepository;

// public MatchService(IUserRepository userRepository,
// IQuestionRepository questionRepository, IQuizRepository quizRepository,
// IQuizQuestionRepository quizQuestionRepository) {
// this.userRepository = userRepository;
// this.questionRepository = questionRepository;
// this.quizRepository = quizRepository;
// this.quizQuestionRepository = quizQuestionRepository;

// }

// public void joinQuiz(UUID userId, UUID quizId){
// AppUser user = this.userRepository.findById(userId)
// .orElseThrow(() -> {
// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with
// id: " + userId);
// });

// Quiz quiz = this.quizRepository.findById(quizId)
// .orElseThrow(() -> {
// throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found with
// id: " + quizId);
// });

// UserMatch userMatch = new UserMatch();
// userMatch.setQuiz(quiz);
// userMatch.setUser(user);
// userMatch.setScore(0);
// try{
// this.
// }
// }
// }
