package com.kamehoot.kamehoot_backend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.services.IQuestionService;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {

        private MockMvc mockMvc;

        @Mock
        private IQuestionService questionService;

        @InjectMocks
        private QuestionController questionController;

        private ObjectMapper objectMapper = new ObjectMapper();

        private List<Question> mockQuestions;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
                mockQuestions = createMockQuestions();
        }

        private List<Question> createMockQuestions() {
                List<Question> questions = new ArrayList<>();

                questions.add(new Question(
                                1L,
                                LocalDateTime.now(),
                                "What is the capital of France?",
                                "Geography",
                                "Paris",
                                Arrays.asList("London", "Berlin", "Madrid"),
                                1));

                questions.add(new Question(
                                2L,
                                LocalDateTime.now(),
                                "What is 2+2?",
                                "Math",
                                "4",
                                Arrays.asList("3", "5", "22"),
                                1));

                for (Question question : questions) {
                        System.out.println(question);
                }

                return questions;
        }

        @Nested
        class GetQuestionTests {

                @Test
                void testGetQuestion_success() throws Exception {
                        // Arrange
                        Question question = mockQuestions.get(0);
                        when(questionService.getQuestion(1L)).thenReturn(question);

                        // Act & Assert
                        mockMvc.perform(get("/questions/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(1))
                                        .andExpect(jsonPath("$.questionText").value("What is the capital of France?"))
                                        .andExpect(jsonPath("$.category").value("Geography"));
                }
        }

        @Nested
        class AddQuestionTests {

                @Test
                void testAddQuestion_success() throws Exception {
                        // Arrange
                        Question newQuestion = new Question(
                                        null,
                                        null,
                                        "New question?",
                                        "Test",
                                        "Answer",
                                        Arrays.asList("Wrong1", "Wrong2"),
                                        1);

                        doNothing().when(questionService).addQuestion(any(Question.class));

                        // Act & Assert
                        mockMvc.perform(post("/questions")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(newQuestion)))
                                        .andExpect(status().isNoContent());

                        verify(questionService, times(1)).addQuestion(any(Question.class));
                }
        }

        @Nested
        class UpdateQuestionTests {

                @Test
                void testUpdateQuestion_success() throws Exception {
                        // Arrange
                        Question question = new Question(
                                        1L,
                                        null,
                                        "Updated question?",
                                        "Test",
                                        "Answer",
                                        Arrays.asList("Wrong1", "Wrong2"),
                                        1);

                        doNothing().when(questionService).updateQuestion(any(Question.class));

                        // Act & Assert
                        mockMvc.perform(put("/questions")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(question)))
                                        .andExpect(status().isNoContent());

                        verify(questionService, times(1)).updateQuestion(any(Question.class));
                }
        }

        @Nested
        class DeleteQuestionTests {

                @Test
                void testDeleteQuestion_success() throws Exception {
                        // Arrange
                        doNothing().when(questionService).deleteQuestionById(1L);

                        // Act & Assert
                        mockMvc.perform(delete("/questions/1"))
                                        .andExpect(status().isNoContent());

                        verify(questionService, times(1)).deleteQuestionById(1L);
                }
        }

}