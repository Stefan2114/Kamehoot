package com.kamehoot.kamehoot_backend.repos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import com.kamehoot.kamehoot_backend.models.Question;

public class QuestionRepositoryTest {

    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        questionRepository = new QuestionRepository();
    }

    @Test
    void testFindAll_populatedInitially() {
        // Act
        List<Question> questions = questionRepository.findAll();

        // Assert
        assertFalse(questions.isEmpty());
        assertTrue(questions.size() >= 18); // Based on `populateQuestions()`
    }

    @Nested
    class UpdateQuestionTests {

        @Test
        void testUpdate_success() {
            // Arrange
            Question original = questionRepository.findAll().get(0);
            original.setQuestionText("Updated question text");

            // Act
            Question updated = questionRepository.update(original);

            // Assert
            assertEquals(original.getId(), updated.getId());
            assertEquals("Updated question text", questionRepository.findById(original.getId()).getQuestionText());
        }

        @Test
        void testUpdate_notFound_shouldThrow() {
            // Arrange
            Question nonExistent = new Question(
                    999L,
                    new Date(),
                    "Ghost question",
                    "Test",
                    "Ghost",
                    List.of("Fake1", "Fake2"),
                    1);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> questionRepository.update(nonExistent));
        }
    }

    @Nested
    class DeleteQuestionTests {

        @Test
        void testDeleteById_success() {
            // Arrange
            Question toDelete = questionRepository.findAll().get(0);
            Long id = toDelete.getId();

            // Act
            questionRepository.deleteById(id);

            // Assert
            assertThrows(RuntimeException.class, () -> questionRepository.findById(id));
        }

        @Test
        void testDeleteById_notFound_shouldThrow() {
            // Act & Assert
            assertThrows(RuntimeException.class, () -> questionRepository.deleteById(9999L));
        }
    }

    @Nested
    class FindQuestionTests {

        @Test
        void testFindById_success() {
            // Arrange
            Question expected = questionRepository.findAll().get(0);

            // Act
            Question actual = questionRepository.findById(expected.getId());

            // Assert
            assertEquals(expected.getId(), actual.getId());
        }

        @Test
        void testFindById_notFound_shouldThrow() {
            // Act & Assert
            assertThrows(RuntimeException.class, () -> questionRepository.findById(9999L));
        }
    }
}