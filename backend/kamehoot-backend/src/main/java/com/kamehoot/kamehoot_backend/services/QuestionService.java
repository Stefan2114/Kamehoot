// 2. Update the QuestionService implementation
package com.kamehoot.kamehoot_backend.services;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
import com.kamehoot.kamehoot_backend.models.WrongAnswer;
import com.kamehoot.kamehoot_backend.repos.ICategoryRepository;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;
import com.kamehoot.kamehoot_backend.repos.IUserRepository;
import com.kamehoot.kamehoot_backend.repos.IWrongAnswerRepository;

@Service
public class QuestionService implements IQuestionService {
        private final IQuestionRepository questionRepository;
        private final ICategoryRepository categoryRepository;
        private final IWrongAnswerRepository wrongAnswerRepository;
        private final PasswordEncoder passwordEncoder;
        private final IUserRepository userRepository;

        @Autowired
        public QuestionService(IQuestionRepository questionRepository, ICategoryRepository categoryRepository,
                        IWrongAnswerRepository wrongAnswerRepository, PasswordEncoder passwordEncoder,
                        IUserRepository userRepository) {
                this.questionRepository = questionRepository;
                this.categoryRepository = categoryRepository;
                this.wrongAnswerRepository = wrongAnswerRepository;
                this.passwordEncoder = passwordEncoder;
                this.userRepository = userRepository;

                // saveJsonQuestions();
        }

        @Override
        public List<Question> getQuestions() {
                return this.questionRepository.findAll();
        }

        @Override
        public List<Question> getQuestions(
                        List<String> categories,
                        List<Integer> difficulties,
                        String searchTerm,
                        String orderBy,
                        String orderDirection) {

                // Get all questions first
                List<Question> allQuestions = this.questionRepository.findAll();

                // Apply filters
                List<Question> filteredQuestions = allQuestions.stream()
                                .filter(q -> categories == null || categories.isEmpty()
                                                || categories.contains(q.getCategory().getName()))
                                .filter(q -> difficulties == null || difficulties.isEmpty()
                                                || difficulties.contains(q.getDifficulty()))
                                .filter(q -> searchTerm == null || searchTerm.isEmpty() ||
                                                q.getQuestionText().toLowerCase().contains(searchTerm.toLowerCase()))
                                .collect(Collectors.toList());

                // Apply sorting
                if (orderBy != null && !orderBy.isEmpty()) {
                        boolean ascending = "asc".equalsIgnoreCase(orderDirection);

                        Comparator<Question> comparator = null;
                        switch (orderBy) {
                                case "difficulty":
                                        comparator = Comparator.comparing(Question::getDifficulty);
                                        break;
                                case "date":
                                        comparator = Comparator.comparing(Question::getCreationDate);
                                        break;
                                default:
                                        comparator = Comparator.comparing(Question::getId);
                                        break;
                        }

                        if (!ascending) {
                                comparator = comparator.reversed();
                        }

                        filteredQuestions.sort(comparator);
                }

                return filteredQuestions;
        }

        @Override
        public void addQuestion(Question question) {
                try {
                        this.questionRepository.save(question);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
        }

        @Override
        public void updateQuestion(Question question) {

                try {
                        this.questionRepository.save(question);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

        @Override
        public void deleteQuestionById(UUID questionId) {
                try {
                        this.questionRepository.deleteById(questionId);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

        @Override
        public Question getQuestion(UUID id) {
                return this.questionRepository.findById(id).orElseThrow(() -> {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found");
                });

        }

        @Override
        public FileSystemResource getIntroVideo() {
                FileSystemResource video = new FileSystemResource("./src/main/resources/What is Kahoot!_.mp4");
                return video;

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
                user.setUsername("stef1");
                user.setPassword(passwordEncoder.encode("stef1"));
                user.setRoles(Set.of("ADMIN"));
                userRepository.save(user);

                AppUser user2 = new AppUser();
                user2.setUsername("stef2");
                user2.setPassword(passwordEncoder.encode("stef2"));
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
                        newQuestion.setCreationDate(question.creationDate());
                        newQuestion.setCategory(category);
                        newQuestion.setCorrectAnswer(question.correctAnswer());
                        newQuestion.setDifficulty(question.difficulty());
                        newQuestion.setQuestionText(question.questionText());

                        Question savedQuestion = this.questionRepository.save(newQuestion);
                        for (String wrongAnswer : question.wrongAnswers()) {
                                WrongAnswer newWrongAnswer = new WrongAnswer();
                                newWrongAnswer.setAnswerText(wrongAnswer);
                                newWrongAnswer.setQuestion(savedQuestion);
                                this.wrongAnswerRepository.save(newWrongAnswer);
                        }
                }
        }
}