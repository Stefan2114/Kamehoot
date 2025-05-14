package com.kamehoot.kamehoot_backend.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_question_difficulty", columnList = "difficulty")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(length = 256, nullable = false)
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 128, nullable = false)
    private String correctAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<WrongAnswer> wrongAnswers;

    @Column(nullable = false)
    private Integer difficulty;

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof Question)) {
            return false;
        }

        return this.id == ((Question) other).id;
    }
}
