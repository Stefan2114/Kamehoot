// 2. Update the QuestionService implementation
package com.kamehoot.kamehoot_backend.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.kamehoot.kamehoot_backend.models.Question;
import com.kamehoot.kamehoot_backend.repos.IQuestionRepository;

@Service
public class QuestionService implements IQuestionService {
        private final IQuestionRepository questionRepository;

        @Autowired
        public QuestionService(IQuestionRepository questionRepository) {
                this.questionRepository = questionRepository;
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
                                                || categories.contains(q.getCategory()))
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
        public List<Question> getQuestionsPaginated(
                        List<String> categories,
                        List<Integer> difficulties,
                        String searchTerm,
                        String orderBy,
                        String orderDirection,
                        int page,
                        int pageSize) {

                // Get filtered and sorted questions using existing method
                List<Question> filteredQuestions = getQuestions(
                                categories,
                                difficulties,
                                searchTerm,
                                orderBy,
                                orderDirection);

                // Apply pagination
                int startIndex = (page - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, filteredQuestions.size());

                // Make sure startIndex is valid
                if (startIndex >= filteredQuestions.size()) {
                        return List.of(); // Return empty list if page is beyond available data
                }

                return filteredQuestions.subList(startIndex, endIndex);
        }

        @Override
        public void addQuestion(Question question) {
                try {
                        this.questionRepository.add(question);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
        }

        @Override
        public void updateQuestion(Question question) {
                if (question.getId() == 0L) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "For update the id should not be null");
                }
                try {
                        this.questionRepository.update(question);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

        @Override
        public void deleteQuestionById(Long questionId) {
                try {
                        this.questionRepository.deleteById(questionId);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

        @Override
        public Question getQuestion(Long id) {
                return this.questionRepository.findById(id);
        }

        @Override
        public FileSystemResource getIntroVideo() {
                FileSystemResource video = new FileSystemResource("./src/main/resources/What is Kahoot!_.mp4");
                return video;

        }
}