package com.kamehoot.kamehoot_backend.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quizzes")
// @Table(name = "quizzes", uniqueConstraints = {
// @UniqueConstraint(columnNames = { "user_id", "question_id" }) })
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
public class Quiz {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private AppUser creator;

    @Column(length = 128, nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> quizQuestions;

}
