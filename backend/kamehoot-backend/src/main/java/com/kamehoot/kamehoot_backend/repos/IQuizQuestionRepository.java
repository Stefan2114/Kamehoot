package com.kamehoot.kamehoot_backend.repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.QuizQuestion;

@Repository
public interface IQuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {

}
