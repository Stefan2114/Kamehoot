package com.kamehoot.kamehoot_backend.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kamehoot.kamehoot_backend.DTOs.QuestionDTO;
import com.kamehoot.kamehoot_backend.models.AppUser;
import com.kamehoot.kamehoot_backend.models.Category;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.models.Visibility;
import com.kamehoot.kamehoot_backend.repos.ICategoryRepository;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;

@Service
public class QuestionService implements IQuestionService {
        private final IQuestionRepository questionRepository;
        private final ICategoryRepository categoryRepository;
        private final PasswordEncoder passwordEncoder;
        private final IUserRepository userRepository;

        @Autowired
        public QuestionService(IQuestionRepository questionRepository, ICategoryRepository categoryRepository,
                        PasswordEncoder passwordEncoder,
                        IUserRepository userRepository) {
                this.questionRepository = questionRepository;
                this.categoryRepository = categoryRepository;
                this.passwordEncoder = passwordEncoder;
                this.userRepository = userRepository;

                // saveJsonQuestions();
        }

        @Override
        public List<Question> getAllQuestions() {
                return this.questionRepository.findAll();
        }

        @Override
        public List<Question> getPublicQuestions() {
                return this.questionRepository.findPublicQuestions();
        }

        @Override
        public List<Question> getUserQuestionList(UUID userId) {
                if (this.userRepository.existsById(userId) == false) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
                }
                return this.questionRepository.findByCreatorId(userId);
        }

        @Override
        public Question getQuestion(UUID id) {
                return this.questionRepository.findById(id).orElseThrow(() -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found");
                });

        }

        // should be changed to use db and pagination
        @Override
        public List<Question> getQuestions(
                        UUID userId,
                        List<String> categories,
                        List<Integer> difficulties,
                        String searchTerm,
                        String orderBy,
                        String orderDirection) {

                List<Question> publicQuestions = this.questionRepository.findPublicQuestions();
                List<Question> userPrivateQuestions = new ArrayList<>();

                if (userId != null) {
                        // Get user's private questions (all questions by this user that are not already
                        // in public)
                        List<Question> allUserQuestions = this.questionRepository.findByCreatorId(userId);
                        userPrivateQuestions = allUserQuestions.stream()
                                        .filter(q -> !q.getVisibility().equals("PUBLIC"))
                                        .collect(Collectors.toList());
                }

                // Combine public questions with user's private questions
                List<Question> combinedQuestions = new ArrayList<>();
                combinedQuestions.addAll(publicQuestions);
                combinedQuestions.addAll(userPrivateQuestions);

                // Remove duplicates if any (in case user's public questions are already
                // included)
                combinedQuestions = combinedQuestions.stream()
                                .distinct()
                                .collect(Collectors.toList());

                return filterAndSortQuestions(combinedQuestions, categories, difficulties, searchTerm, orderBy,
                                orderDirection);
        }

        private List<Question> filterAndSortQuestions(
                        List<Question> questions,
                        List<String> categories,
                        List<Integer> difficulties,
                        String searchTerm,
                        String orderBy,
                        String orderDirection) {

                List<Question> filteredQuestions = questions.stream()
                                .filter(q -> categories == null || categories.isEmpty()
                                                || categories.contains(q.getCategory().getName()))
                                .filter(q -> difficulties == null || difficulties.isEmpty()
                                                || difficulties.contains(q.getDifficulty()))
                                .filter(q -> searchTerm == null || searchTerm.isEmpty() ||
                                                q.getQuestionText().toLowerCase().contains(searchTerm.toLowerCase()))
                                .collect(Collectors.toList());

                if (orderBy != null && !orderBy.isEmpty()) {
                        Comparator<Question> comparator = null;
                        switch (orderBy) {
                                case "difficulty":
                                        comparator = Comparator.comparing(Question::getDifficulty);
                                        break;
                                case "category":
                                        comparator = Comparator.comparing(q -> q.getCategory().getName());
                                        break;
                                case "questionText":
                                        comparator = Comparator.comparing(Question::getQuestionText);
                                        break;
                                default:
                                        comparator = Comparator.comparing(Question::getCreationDate);
                                        break;
                        }

                        boolean ascending = "asc".equalsIgnoreCase(orderDirection);
                        if (!ascending) {
                                comparator = comparator.reversed();
                        }
                        filteredQuestions.sort(comparator);
                }

                return filteredQuestions;
        }

        @Override
        public void updateQuestion(QuestionDTO questionDTO) {

                if (questionDTO.id() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Question id is null");
                }
                Question question = this.questionRepository.findById(questionDTO.id())
                                .orElseThrow(() -> {
                                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Question not found with id: " + questionDTO.id());
                                });

                Category category = this.categoryRepository.findByName(questionDTO.category());
                if (category == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Category not found with name: " + questionDTO.category());
                }

                question.setCreationDate(questionDTO.creationDate());
                question.setCategory(category);
                question.setCorrectAnswer(questionDTO.correctAnswer());
                question.setDifficulty(questionDTO.difficulty());
                question.setQuestionText(questionDTO.questionText());
                question.setWrongAnswers(questionDTO.wrongAnswers());

                try {
                        questionRepository.save(question);
                } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Failed to save the question: " + e.getMessage());
                }
        }

        @Override
        public void addUserQuestion(String username, QuestionDTO questionDTO) {
                AppUser user = this.userRepository.findByUsername(username)
                                .orElseThrow(() -> {
                                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "User not found with username: " + username);
                                });
                Category category = this.categoryRepository.findByName(questionDTO.category());
                if (category == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Category not found with name: " + questionDTO.category());
                }

                Question question = new Question();

                question.setCreator(user);
                question.setCreationDate(questionDTO.creationDate());
                question.setCategory(category);
                question.setCorrectAnswer(questionDTO.correctAnswer());
                question.setDifficulty(questionDTO.difficulty());
                question.setQuestionText(questionDTO.questionText());
                question.setVisibility(Visibility.PRIVATE);
                question.setWrongAnswers(questionDTO.wrongAnswers());

                try {
                        questionRepository.save(question);
                } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Failed to save the question: " + e.getMessage());
                }
        }

        @Override
        public void deleteQuestionById(UUID questionId) {
                try {
                        this.questionRepository.deleteById(questionId);
                } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Couldn't delete question with id: " + questionId + " Error: "
                                                        + e.getMessage());

                }
        }

        private List<QuestionDTO> loadQuestionsFromJson() {
                try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        InputStream inputStream = getClass().getResourceAsStream("/questions.json");
                        return mapper.readValue(inputStream,
                                        new TypeReference<List<QuestionDTO>>() {
                                        });

                } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                }
        }

        private void saveJsonQuestions() {

                Category category1 = new Category();
                category1.setName("Math");
                Category category2 = new Category();
                category2.setName("Football");

                AppUser user = new AppUser();
                user.setUsername("admin");
                user.setPassword(passwordEncoder.encode("admin"));
                user.setRoles(Set.of("ADMIN"));
                AppUser admin = userRepository.save(user);

                AppUser user2 = new AppUser();
                user2.setUsername("stef");
                user2.setPassword(passwordEncoder.encode("stef"));
                user2.setRoles(Set.of("USER"));
                userRepository.save(user2);

                this.categoryRepository.save(category1);
                this.categoryRepository.save(category2);
                System.out.println("Categories saved");

                List<QuestionDTO> jsonQuestions = loadQuestionsFromJson();
                System.out.println("JSON Questions got");
                for (QuestionDTO question : jsonQuestions) {
                        Category category = this.categoryRepository.findByName(question.category());
                        Question newQuestion = new Question();
                        newQuestion.setCreator(admin);
                        newQuestion.setVisibility(Visibility.PUBLIC);
                        newQuestion.setCreationDate(question.creationDate());
                        newQuestion.setCategory(category);
                        newQuestion.setCorrectAnswer(question.correctAnswer());
                        newQuestion.setDifficulty(question.difficulty());
                        newQuestion.setQuestionText(question.questionText());
                        newQuestion.setWrongAnswers(question.wrongAnswers());
                        this.questionRepository.save(newQuestion);

                }
        }
}