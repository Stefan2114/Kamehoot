package com.kamehoot.kamehoot_backend.services;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;

public interface IGameService {

    void connect(UUID gameSessionId, WebSocketSession session);

    void disconnect(WebSocketSession session);

    String createGame(UUID userId, UUID quizId, Integer timeLimit);

    void startGame(UUID userId, UUID gameSessionId);

    void joinGame(UUID userId, UUID gameSessionId);

    void submitAnswer(UUID userId, UUID gameSessionId, UUID questionId, String answer,
            ZonedDateTime answerTime);

    void nextQuestion(UUID userId, UUID gameSessionId);

    GameSessionDTO getGameSessionDTO(String gameCode);

    boolean isHost(UUID userId, UUID gameSessionId);

    void sendEmoji(UUID gameSessionId, String emoji);

}