package com.kamehoot.kamehoot_backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
@Table(name = "quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime completedAt; // NULL if not completed

    @Column(nullable = false)
    private Integer totalScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<QuizAttemptAnswer> answers = new ArrayList<>();

    public boolean isCompleted() {
        return status == AttemptStatus.COMPLETED;
    }

    public double getScorePercentage() {

        Integer maxPossibleScore = quiz.getMaxPossibleScore();
        if (maxPossibleScore == 0) {
            return 0.0;
        }

        return totalScore / maxPossibleScore * 100;
    }

    public Integer getCorrectAnswersCount() {
        return (int) answers.stream().filter(QuizAttemptAnswer::getIsCorrect).count();
    }
}
