package com.kamehoot.kamehoot_backend.repos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

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
                LAST_ID_USED = 0L;
                populateQuestions();

        }

        private int getQuestionPosition(Question question) {

                int pos = 0;
                for (int i = 0; i < this.questions.size(); i++) {
                        if (question.equals(this.questions.get(i))) {
                                pos = i;
                                break;
                        }
                }

                return pos;
        }

        @Override
        public Question add(Question question) {

                int pos = getQuestionPosition(question);
                System.out.println(pos);

                if (pos != 0) {
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

                if (pos == 0) {
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

        private void populateQuestions() {
                Date currentDate = new Date();

                this.questions.add(
                                new Question(getNewId(), currentDate, "What is the derivative of x^2?", "Math", "2x",
                                                List.of("x^2", "x"), 1));
                this.questions
                                .add(new Question(getNewId(), currentDate, "Who won the FIFA World Cup in 2018?",
                                                "Football", "France",
                                                List.of("Brazil", "Germany"), 3));
                this.questions.add(
                                new Question(getNewId(), currentDate, "Solve for x: 2x + 3 = 7", "Math", "2",
                                                List.of("3", "4"), 3));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "Which club has won the most UEFA Champions League titles?",
                                                "Football", "Real Madrid", List.of("Barcelona", "Manchester United"),
                                                3));
                currentDate = new Date();
                this.questions.add(
                                new Question(getNewId(), currentDate, "What is the integral of 3x^2?", "Math", "x^3",
                                                List.of("3x", "x^2"), 2));
                this.questions.add(
                                new Question(getNewId(), currentDate,
                                                "Who is the all-time top scorer in the UEFA Champions League?",
                                                "Football", "Cristiano Ronaldo",
                                                List.of("Lionel Messi", "Robert Lewandowski"), 3));
                this.questions.add(
                                new Question(getNewId(), currentDate, "What is the square root of 144?", "Math", "12",
                                                List.of("14", "10"), 2));
                this.questions.add(
                                new Question(getNewId(), currentDate, "Which country has won the most FIFA World Cups?",
                                                "Football",
                                                "Brazil", List.of("Germany", "Argentina"), 1));
                currentDate = new Date();
                this.questions.add(
                                new Question(getNewId(), currentDate, "If f(x) = x^3, what is f'(x)?", "Math", "3x^2",
                                                List.of("x^2", "x^3"), 2));
                this.questions
                                .add(new Question(getNewId(), currentDate, "Who won the Ballon d'Or in 2021?",
                                                "Football",
                                                "Lionel Messi",
                                                List.of("Robert Lewandowski", "Karim Benzema"), 3));
                this.questions.add(new Question(getNewId(), currentDate, "What is the sum of the angles in a triangle?",
                                "Math",
                                "180 degrees", List.of("90 degrees", "270 degrees"), 3));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "Who holds the record for most goals in a single World Cup?",
                                                "Football", "Just Fontaine", List.of("Pele", "Miroslav Klose"), 3));
                currentDate = new Date();
                this.questions
                                .add(new Question(getNewId(), currentDate, "Solve for x: 3x - 5 = 10", "Math", "5",
                                                List.of("3", "7"),
                                                3));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "Which player has won the most Ballon d'Or awards?",
                                                "Football",
                                                "Lionel Messi", List.of("Cristiano Ronaldo", "Johan Cruyff"), 1));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "What is the value of pi (rounded to 3 decimal places)?",
                                                "Math",
                                                "3.142", List.of("3.141", "3.143"), 1));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "Which team won the UEFA Champions League in 2020?",
                                                "Football",
                                                "Bayern Munich", List.of("Liverpool", "Paris Saint-Germain"), 1));
                currentDate = new Date();
                this.questions.add(new Question(getNewId(), currentDate, "What is 9! (9 factorial)?", "Math", "362880",
                                List.of("40320", "362800"), 1));
                this.questions
                                .add(new Question(getNewId(), currentDate, "Who scored the famous 'Hand of God' goal?",
                                                "Football",
                                                "Diego Maradona", List.of("Pelé", "Zinedine Zidane"), 2));
                this.questions.add(new Question(getNewId(), currentDate, "What is the logarithm base 10 of 1000?",
                                "Math", "3",
                                List.of("2", "4"), 3));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "Which club has the most Premier League titles?", "Football",
                                                "Manchester United", List.of("Chelsea", "Liverpool"), 2));
                this.questions.add(new Question(getNewId(), currentDate, "If sin(30°) = 0.5, what is cos(60°)?", "Math",
                                "0.5",
                                List.of("0.866", "1"), 2));
                currentDate = new Date();
                this.questions
                                .add(new Question(getNewId(), currentDate, "Who won the 2014 FIFA World Cup?",
                                                "Football", "Germany",
                                                List.of("Argentina", "Brazil"), 2));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "What is the determinant of the matrix [[1,2],[3,4]]?",
                                                "Math",
                                                "-2", List.of("2", "0"), 1));
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "Which country hosted the 2006 FIFA World Cup?", "Football",
                                                "Germany", List.of("France", "South Africa"), 3));
                currentDate = new Date();
                this.questions
                                .add(new Question(getNewId(), currentDate,
                                                "What is the formula for the area of a circle?", "Math",
                                                "πr²",
                                                List.of("2πr", "πd"), 3));
        }

}
