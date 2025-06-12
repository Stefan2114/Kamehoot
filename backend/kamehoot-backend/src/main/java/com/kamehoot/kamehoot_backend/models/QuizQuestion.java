package com.kamehoot.kamehoot_backend.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_questions")
@Check(constraints = "difficulty BETWEEN 1 AND 3")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @Column(length = 512, nullable = false)
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 128, nullable = false)
    private String correctAnswer;

    @ElementCollection
    @CollectionTable(name = "wrong_answers", joinColumns = { @JoinColumn(name = "question_id") })
    @Column(name = "wrong_answer", length = 128, nullable = false)
    // @Size()
    private List<String> wrongAnswers = new ArrayList<>();

    @Column(nullable = false)
    private Integer difficulty;

}
