package com.kamehoot.kamehoot_backend.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;

public interface IGameService {

    void connect(UUID gameSessionId, WebSocketSession session);

    void disconnect(WebSocketSession session);

    String createGame(String hostUsername, UUID quizId, Integer timeLimit);

    void startGame(String hostUsername, UUID gameSessionId);

    void joinGame(String username, String gameCode);

    void submitAnswer(String username, UUID gameSessionId, UUID questionId, String answer,
            LocalDateTime answerTime);

    void nextQuestion(String hostUsername, UUID gameSessionId);

    GameSessionDTO getGameSessionDTO(String gameCode);

}