package com.kamehoot.kamehoot_backend.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.kamehoot.kamehoot_backend.DTOs.CreateGameRequest;
import com.kamehoot.kamehoot_backend.DTOs.GameSessionDTO;
import com.kamehoot.kamehoot_backend.DTOs.QuestionResultDTO;
import com.kamehoot.kamehoot_backend.DTOs.SubmitAnswerRequest;
import com.kamehoot.kamehoot_backend.services.IGameService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IGameService gameService;

    public GameController(IGameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameSessionDTO> createGame(@RequestBody @Valid CreateGameRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            GameSessionDTO gameSession = gameService.createGame(username, request);
            return ResponseEntity.ok(gameSession);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/join")
    public ResponseEntity<GameSessionDTO> joinGame(@RequestBody @Valid String gameCode) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            GameSessionDTO gameSession = gameService.joinGame(username, gameCode);
            return ResponseEntity.ok(gameSession);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{gameSessionId}/start")
    public ResponseEntity<Void> startGame(@PathVariable UUID gameSessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            gameService.startGame(username, gameSessionId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/answer")
    public ResponseEntity<Void> submitAnswer(@RequestBody @Valid SubmitAnswerRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            gameService.submitAnswer(username, request);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{gameSessionId}/results")
    public ResponseEntity<QuestionResultDTO> getQuestionResults(@PathVariable UUID gameSessionId) {
        QuestionResultDTO results = this.gameService.getQuestionResults(gameSessionId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{gameSessionId}/next")
    public ResponseEntity<Void> nextQuestion(@PathVariable UUID gameSessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {
            String username = jwtAuth.getName();
            gameService.nextQuestion(username, gameSessionId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{gameSessionId}")
    public ResponseEntity<GameSessionDTO> getGameSession(@PathVariable UUID gameSessionId) {

        // This needs to be implemented in the service
        return ResponseEntity.notFound().build();

    }
}
