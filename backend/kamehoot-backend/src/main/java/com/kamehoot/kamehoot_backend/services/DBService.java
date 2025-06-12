package com.kamehoot.kamehoot_backend.services;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
public class DBService {

    private final IQuestionRepository questionRepository;
    private final ICategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;

    @Autowired
    public DBService(IQuestionRepository questionRepository, ICategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder,
            IUserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

        // saveJsonQuestions();
    }

    private void saveJsonQuestions() {

        Category category1 = new Category();
        category1.setName("Math");
        Category category2 = new Category();
        category2.setName("Football");

        AppUser user = new AppUser();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("Admin1234!"));
        user.setRoles(Set.of("ADMIN"));
        AppUser admin = userRepository.save(user);

        AppUser user2 = new AppUser();
        user2.setUsername("stef");
        user2.setPassword(passwordEncoder.encode("Stefan2004!"));
        user2.setRoles(Set.of("USER"));
        userRepository.save(user2);

        Category mathCategory = this.categoryRepository.save(category1);
        this.categoryRepository.save(category2);
        System.out.println("Categories saved");

        Question userQuestion = new Question();
        userQuestion.setCreator(user2);
        userQuestion.setVisibility(Visibility.PRIVATE);
        userQuestion.setCreationDate(LocalDateTime.now());
        userQuestion.setCategory(mathCategory);
        userQuestion.setCorrectAnswer("64");
        userQuestion.setDifficulty(1);
        userQuestion.setQuestionText("What is 8*8?");
        userQuestion.setWrongAnswers(Arrays.asList("81", "56"));
        this.questionRepository.save(userQuestion);

        List<QuestionDTO> jsonQuestions = loadQuestionsFromJson();
        System.out.println("JSON Questions got");
        for (QuestionDTO question : jsonQuestions) {
            Category category = this.categoryRepository.findByName(question.category());
            Question newQuestion = new Question();
            newQuestion.setCreator(admin);
            newQuestion.setCreationDate(question.creationDate());
            newQuestion.setCategory(category);
            newQuestion.setCorrectAnswer(question.correctAnswer());
            newQuestion.setDifficulty(question.difficulty());
            newQuestion.setQuestionText(question.questionText());
            newQuestion.setWrongAnswers(question.wrongAnswers());
            this.questionRepository.save(newQuestion);

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
}
