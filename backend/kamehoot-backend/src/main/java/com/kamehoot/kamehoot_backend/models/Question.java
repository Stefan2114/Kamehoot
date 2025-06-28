package com.kamehoot.kamehoot_backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kamehoot.kamehoot_backend.enums.Visibility;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "questions")
@Check(constraints = "visibility IN ('PRIVATE', 'PUBLIC') AND difficulty BETWEEN 1 AND 3")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private AppUser creator;

    @Enumerated(EnumType.STRING)
    @Column(length = 64, nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

    @Column(nullable = false)
    private LocalDateTime creationDate;

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

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (!(other instanceof Question)) {
            return false;
        }

        return this.id != null && this.id.equals(((Question) other).id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
