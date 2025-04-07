package com.kamehoot.kamehoot_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.web.server.ResponseStatusException;

import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private IQuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private List<Question> mockQuestions;

    @BeforeEach
    void setUp() {
        mockQuestions = createMockQuestions();
    }

    private List<Question> createMockQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question(
                1L,
                new Date(),
                "What is the capital of France?",
                "Geography",
                "Paris",
                Arrays.asList("London", "Berlin", "Madrid"),
                1));

        questions.add(new Question(
                2L,
                new Date(),
                "What is 2+2?",
                "Math",
                "4",
                Arrays.asList("3", "5", "22"),
                1));

        questions.add(new Question(
                3L,
                new Date(),
                "What is the chemical symbol for water?",
                "Chemistry",
                "H2O",
                Arrays.asList("CO2", "NaCl", "O2"),
                2));

        questions.add(new Question(
                4L,
                new Date(),
                "Which planet is known as the Red Planet?",
                "Astronomy",
                "Mars",
                Arrays.asList("Venus", "Jupiter", "Mercury"),
                2));

        questions.add(new Question(
                5L,
                new Date(),
                "What is quantum entanglement?",
                "Physics",
                "A quantum phenomenon where particles remain connected",
                Arrays.asList("A type of chemical bond", "A mathematical theorem", "A biological process"),
                3));

        return questions;
    }

    @Nested
    class GetQuestionsTests {

        @BeforeEach
        void setUpGetQuestionsTests() {
            when(questionRepository.findAll()).thenReturn(mockQuestions);
        }

        @Test
        void testGetQuestions_noFilters() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, null, null, null, null);

            // Assert
            assertEquals(5, result.size());
            assertEquals(mockQuestions, result);
        }

        @Test
        void testGetQuestions_withCategoryFilter() {
            // Act
            List<Question> result = questionService.getQuestions(
                    Arrays.asList("Geography"), null, null, null, null);

            // Assert
            assertEquals(1, result);
            assertEquals("Geography", result.get(0).getCategory());
        }

        @Test
        void testGetQuestions_withDifficultyFilter() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, Arrays.asList(3), null, null, null);

            // Assert
            assertEquals(1, result.size());
            assertEquals(3, result.get(0).getDifficulty());
        }

        @Test
        void testGetQuestions_withSearchTerm() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, null, "planet", null, null);

            // Assert
            assertEquals(1, result.size());
            assertTrue(result.get(0).getQuestionText().contains("planet"));
        }

        @Test
        void testGetQuestions_withSorting_difficulty_asc() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, null, null, "difficulty", "asc");

            // Assert
            assertEquals(5, result.size());
            assertEquals(1, result.get(0).getDifficulty()); // First should be easy (1)
            assertEquals(3, result.get(4).getDifficulty()); // Last should be hard (3)
        }

        @Test
        void testGetQuestions_withSorting_difficulty_desc() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, null, null, "difficulty", "desc");

            // Assert
            assertEquals(5, result.size());
            assertEquals(3, result.get(0).getDifficulty()); // First should be hard (3)
            assertEquals(1, result.get(4).getDifficulty()); // Last should be easy (1)
        }

        @Test
        void testGetQuestions_withPagination() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, null, null, null, null);

            // Assert
            assertEquals(5, result.size()); // tricky
            assertEquals(2, result.size());
        }

        @Test
        void testGetQuestions_withSecondPage() {
            // Act
            List<Question> result = questionService.getQuestions(
                    null, null, null, null, null);

            // Assert
            assertEquals(5, result.size()); // tricky
            assertEquals(2, result.size());
            assertEquals(mockQuestions.get(2).getId(), result.get(0).getId());
        }

        @Test
        void testGetQuestions_withCombinedFilters() {
            // Act
            List<Question> result = questionService.getQuestions(
                    Arrays.asList("Math", "Geography"),
                    Arrays.asList(1),
                    null,
                    "difficulty",
                    "asc");

            // Assert
            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getDifficulty());
            assertTrue(Arrays.asList("Math", "Geography").contains(result.get(0).getCategory()));
        }
    }

    @Nested
    class AddQuestionTests {

        @Test
        void testAddQuestion_failure() {
            // Arrange
            Question newQuestion = new Question(
                    null,
                    new Date(),
                    "New question?",
                    "Test",
                    "Answer",
                    Arrays.asList("Wrong1", "Wrong2"),
                    1);

            doThrow(new RuntimeException("Error")).when(questionRepository).add(any(Question.class));

            // Act & Assert
            assertThrows(ResponseStatusException.class, () -> questionService.addQuestion(newQuestion));
        }
    }

    @Nested
    class UpdateQuestionTests {

        @Test
        void testUpdateQuestion_nullId() {
            // Arrange
            Question question = new Question(
                    0L,
                    new Date(),
                    "Updated question?",
                    "Test",
                    "Answer",
                    Arrays.asList("Wrong1", "Wrong2"),
                    1);

            // Act & Assert
            assertThrows(ResponseStatusException.class, () -> questionService.updateQuestion(question));
            verify(questionRepository, never()).update(any());
        }

        @Test
        void testUpdateQuestion_notFound() {
            // Arrange
            Question question = new Question(
                    1L,
                    new Date(),
                    "Updated question?",
                    "Test",
                    "Answer",
                    Arrays.asList("Wrong1", "Wrong2"),
                    1);

            doThrow(new RuntimeException("Not found")).when(questionRepository).update(any(Question.class));

            // Act & Assert
            assertThrows(ResponseStatusException.class, () -> questionService.updateQuestion(question));
        }
    }

    @Nested
    class DeleteQuestionTests {

        @Test
        void testDeleteQuestionById_success() {
            // Arrange
            Long id = 1L;
            doNothing().when(questionRepository).deleteById(id);

            // Act & Assert
            assertDoesNotThrow(() -> questionService.deleteQuestionById(id));
            verify(questionRepository, times(1)).deleteById(id);
        }

        @Test
        void testDeleteQuestionById_notFound() {
            // Arrange
            Long id = 99L;
            doThrow(new RuntimeException("Not found")).when(questionRepository).deleteById(id);

            // Act & Assert
            assertThrows(ResponseStatusException.class, () -> questionService.deleteQuestionById(id));
        }
    }

    @Nested
    class GetQuestionTests {

        @Test
        void testGetQuestion_success() {
            // Arrange
            Long id = 1L;
            Question expected = mockQuestions.get(0);
            when(questionRepository.findById(id)).thenReturn(expected);

            // Act
            Question result = questionService.getQuestion(id);

            // Assert
            assertEquals(expected, result);
        }
    }

}