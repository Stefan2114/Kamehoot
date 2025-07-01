package com.kamehoot.kamehoot_backend.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.GameAnswer;

@Repository
public interface IGameAnswerRepository extends JpaRepository<GameAnswer, UUID> {

    @Query("SELECT ga FROM GameAnswer ga WHERE ga.gamePlayer.gameSession.id = :gameSessionId AND ga.quizQuestion.id = :questionId")
    List<GameAnswer> getAllByGameSessionIdAndQuestionId(@Param("gameSessionId") UUID gameSessionId,
            @Param("questionId") UUID questionId);

    List<GameAnswer> findAllByGamePlayerId(UUID id);

}
