package com.kamehoot.kamehoot_backend.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.GameSession;

@Repository
public interface IGameSessionRepository extends JpaRepository<GameSession, UUID> {

    Optional<GameSession> findByGameCode(String gameCode);

    boolean existsByGameCode(String code);

}
