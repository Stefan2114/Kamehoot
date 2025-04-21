package com.kamehoot.kamehoot_backend.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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
import lombok.ToString;

@Entity
@Table(name = "wrong_answers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WrongAnswer {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 256, nullable = false)
    private String answerText;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

}
