package com.kamehoot.kamehoot_backend.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quiz_questions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "quiz_id", "question_id" }) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// this is good, the user can only see his questions and the public once. So
// there are 2 types of questions public and private
// there will be quizzes that are all public. An user can create a quiz using
// public questions or personal questions
// another user can play other user quiz but can not use it's questions
// directly. They have to create their own questions that are similar
public class QuizQuestion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

}
