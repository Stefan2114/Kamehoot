package com.kamehoot.kamehoot_backend.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.Quiz;

@Repository
public interface IQuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByCreatorId(UUID userId);

}
