package com.kamehoot.kamehoot_backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kamehoot.kamehoot_backend.models.Question;

@Service
public class QuestionService implements IQuestionService {

    @Override
    public List<Question> getQuestions() {

        List<Question> questions = new ArrayList<>();

        Date currentDate = new Date();

        questions.add(
                new Question(1L, currentDate, "What is the derivative of x^2?", "Math", "2x", List.of("x^2", "x"), 3));
        questions.add(new Question(2L, currentDate, "Who won the FIFA World Cup in 2018?", "Football", "France",
                List.of("Brazil", "Germany"), 3));
        questions.add(new Question(3L, currentDate, "Solve for x: 2x + 3 = 7", "Math", "2", List.of("3", "4"), 3));
        questions.add(new Question(4L, currentDate, "Which club has won the most UEFA Champions League titles?",
                "Football", "Real Madrid", List.of("Barcelona", "Manchester United"), 3));
        questions.add(
                new Question(5L, currentDate, "What is the integral of 3x^2?", "Math", "x^3", List.of("3x", "x^2"), 3));
        questions.add(new Question(6L, currentDate, "Who is the all-time top scorer in the UEFA Champions League?",
                "Football", "Cristiano Ronaldo", List.of("Lionel Messi", "Robert Lewandowski"), 3));
        questions.add(
                new Question(7L, currentDate, "What is the square root of 144?", "Math", "12", List.of("14", "10"), 3));
        questions.add(new Question(8L, currentDate, "Which country has won the most FIFA World Cups?", "Football",
                "Brazil", List.of("Germany", "Argentina"), 3));
        questions.add(new Question(9L, currentDate, "If f(x) = x^3, what is f'(x)?", "Math", "3x^2",
                List.of("x^2", "x^3"), 3));
        questions.add(new Question(10L, currentDate, "Who won the Ballon d'Or in 2021?", "Football", "Lionel Messi",
                List.of("Robert Lewandowski", "Karim Benzema"), 3));
        questions.add(new Question(11L, currentDate, "What is the sum of the angles in a triangle?", "Math",
                "180 degrees", List.of("90 degrees", "270 degrees"), 3));
        questions.add(new Question(12L, currentDate, "Who holds the record for most goals in a single World Cup?",
                "Football", "Just Fontaine", List.of("Pele", "Miroslav Klose"), 3));
        questions.add(new Question(13L, currentDate, "Solve for x: 3x - 5 = 10", "Math", "5", List.of("3", "7"), 3));
        questions.add(new Question(14L, currentDate, "Which player has won the most Ballon d'Or awards?", "Football",
                "Lionel Messi", List.of("Cristiano Ronaldo", "Johan Cruyff"), 3));
        questions.add(new Question(15L, currentDate, "What is the value of pi (rounded to 3 decimal places)?", "Math",
                "3.142", List.of("3.141", "3.143"), 3));
        questions.add(new Question(16L, currentDate, "Which team won the UEFA Champions League in 2020?", "Football",
                "Bayern Munich", List.of("Liverpool", "Paris Saint-Germain"), 3));
        questions.add(new Question(17L, currentDate, "What is 9! (9 factorial)?", "Math", "362880",
                List.of("40320", "362800"), 3));
        questions.add(new Question(18L, currentDate, "Who scored the famous 'Hand of God' goal?", "Football",
                "Diego Maradona", List.of("Pelé", "Zinedine Zidane"), 3));
        questions.add(new Question(19L, currentDate, "What is the logarithm base 10 of 1000?", "Math", "3",
                List.of("2", "4"), 3));
        questions.add(new Question(20L, currentDate, "Which club has the most Premier League titles?", "Football",
                "Manchester United", List.of("Chelsea", "Liverpool"), 3));
        questions.add(new Question(21L, currentDate, "If sin(30°) = 0.5, what is cos(60°)?", "Math", "0.5",
                List.of("0.866", "1"), 3));
        questions.add(new Question(22L, currentDate, "Who won the 2014 FIFA World Cup?", "Football", "Germany",
                List.of("Argentina", "Brazil"), 3));
        questions.add(new Question(23L, currentDate, "What is the determinant of the matrix [[1,2],[3,4]]?", "Math",
                "-2", List.of("2", "0"), 3));
        questions.add(new Question(24L, currentDate, "Which country hosted the 2006 FIFA World Cup?", "Football",
                "Germany", List.of("France", "South Africa"), 3));
        questions.add(new Question(25L, currentDate, "What is the formula for the area of a circle?", "Math", "πr²",
                List.of("2πr", "πd"), 3));

        return questions;
    }

}
