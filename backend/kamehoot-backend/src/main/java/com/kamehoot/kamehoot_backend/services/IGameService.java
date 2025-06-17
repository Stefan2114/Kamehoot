package com.kamehoot.kamehoot_backend.services;

import java.util.UUID;

import com.kamehoot.kamehoot_backend.DTOs.CreateGameRequest;
import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;
import com.kamehoot.kamehoot_backend.DTOs.QuestionResultDTO;
import com.kamehoot.kamehoot_backend.DTOs.SubmitAnswerRequest;

public interface IGameService {

    GameSessionDTO createGame(String hostUsername, CreateGameRequest request);

    GameSessionDTO joinGame(String username, String gameCode);

    void startGame(String hostUsername, UUID gameSessionId);

    void submitAnswer(String username, SubmitAnswerRequest request);

    QuestionResultDTO getQuestionResults(UUID gameSessionId);

    void nextQuestion(String hostUsername, UUID gameSessionId);

    int calculatePoints(long responseTime, int timeLimit);

}