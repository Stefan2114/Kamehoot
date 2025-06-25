package com.kamehoot.kamehoot_backend.controllers;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.DTOs.CreateGameRequest;
import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;
import com.kamehoot.kamehoot_backend.DTOs.SubmitAnswerRequest;
import com.kamehoot.kamehoot_backend.services.IGameService;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IGameService gameService;

    public GameController(IGameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame(@RequestBody CreateGameRequest request,
            Principal principal) {
        try {
            String gameCode = gameService.createGame(
                    principal.getName(),
                    request.quizId(),
                    request.questionTimeLimit());
            return ResponseEntity.ok(gameCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/join/{gameCode}")
    public ResponseEntity<Void> joinGame(@PathVariable String gameCode,
            Principal principal) {
        try {
            gameService.joinGame(principal.getName(), gameCode);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<Void> startGame(@PathVariable UUID id,
            Principal principal) {
        try {
            gameService.startGame(principal.getName(), id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<Void> submitAnswer(@RequestBody SubmitAnswerRequest request,
            Principal principal) {
        try {
            gameService.submitAnswer(
                    principal.getName(),
                    request.gameSessionId(),
                    request.questionId(),
                    request.answer(), request.answerTime());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/next/{id}")
    public ResponseEntity<Void> nextQuestion(@PathVariable UUID id,
            Principal principal) {
        try {
            gameService.nextQuestion(principal.getName(), id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<GameSessionDTO> getGameSession(@PathVariable String gameCode) {
        try {
            GameSessionDTO gameSession = gameService.getGameSessionDTO(gameCode);
            return ResponseEntity.ok(gameSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}