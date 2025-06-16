package com.kamehoot.kamehoot_backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Check(constraints = "visibility IN ('PRIVATE', 'PUBLIC')")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 64, nullable = false)
    @JsonIgnore
    private Visibility visibility = Visibility.PRIVATE;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private AppUser creator;

    @Column(length = 128, nullable = false)
    private String title;

    @Column(length = 512, nullable = false)
    private String description = "";

    @Column(nullable = false)
    private LocalDateTime creationDate;

    // @Column(nullable = false)
    // private LocalDateTime lastModified;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuizAttempt> attempts = new ArrayList<>();

    public Integer getMaxPossibleScore() {
        return questions.size();
    }

}
