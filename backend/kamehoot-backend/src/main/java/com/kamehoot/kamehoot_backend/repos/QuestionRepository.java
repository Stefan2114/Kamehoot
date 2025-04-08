package com.kamehoot.kamehoot_backend.repos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kamehoot.kamehoot_backend.models.Question;

@Repository
public class QuestionRepository implements IQuestionRepository {

        private List<Question> questions;

        private static Long LAST_ID_USED;

        private synchronized Long getNewId() {
                LAST_ID_USED++;
                return Long.valueOf(LAST_ID_USED);

        }

        public QuestionRepository() {
                this.questions = new ArrayList<>();
                loadQuestionsFromJson();
                if (questions.isEmpty()) {
                        LAST_ID_USED = 0L;
                } else {
                        LAST_ID_USED = this.questions.get(this.questions.size() - 1).getId();
                }

        }

        private void loadQuestionsFromJson() {
                try {
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());
                        InputStream inputStream = getClass().getResourceAsStream("/questions.json");
                        this.questions = mapper.readValue(inputStream, new TypeReference<List<Question>>() {
                        });
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                }
        }

        private int getQuestionPosition(Question question) {

                int pos = -1;
                for (int i = 0; i < this.questions.size(); i++) {
                        System.out.println(i);
                        if (question.equals(this.questions.get(i))) {
                                pos = i;
                                System.out.println("YESSS");
                                break;
                        }
                }

                return pos;
        }

        @Override
        public Question add(Question question) {

                int pos = getQuestionPosition(question);
                System.out.println(pos);

                if (pos != -1) {
                        System.out.println("exception question id already exists");
                        throw new RuntimeException("There already exists that question id");
                }

                System.out.println("adds the question");
                question.setId(getNewId());
                this.questions.add(question);

                return question;

        }

        @Override
        public Question update(Question question) {
                int pos = getQuestionPosition(question);
                System.out.println(pos);

                if (pos == -1) {
                        System.out.println("will through exception for id not found");
                        throw new RuntimeException("Question with id: " + question.getId() + " not found");

                }

                System.out.println("updates the question");

                Question returnQuestion = this.questions.get(pos);
                this.questions.set(pos, question);

                return returnQuestion;
        }

        @Override
        public void deleteById(Long questionId) {

                for (int i = 0; i < this.questions.size(); i++) {
                        if (this.questions.get(i).getId() == questionId) {
                                this.questions.remove(i);
                                return;
                        }
                }

                throw new RuntimeException("Question with id: " + questionId + " was not found");

        }

        @Override
        public List<Question> findAll() {
                return this.questions;
        }

        @Override
        public Question findById(Long id) {
                for (int i = 0; i < this.questions.size(); i++) {

                        Question question = this.questions.get(i);
                        if (question.getId() == id) {
                                return question;
                        }
                }

                throw new RuntimeException("Id not found");
        }

}
