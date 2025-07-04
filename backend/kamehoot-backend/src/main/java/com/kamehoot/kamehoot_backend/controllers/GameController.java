package com.kamehoot.kamehoot_backend.controllers;

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
import com.kamehoot.kamehoot_backend.utils.AuthenticationUtil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IGameService gameService;
    private final AuthenticationUtil authenticationUtil;

    public GameController(IGameService gameService, AuthenticationUtil authenticationUtil) {
        this.gameService = gameService;
        this.authenticationUtil = authenticationUtil;
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<GameSessionDTO> getGameSession(
            @PathVariable @NotBlank @Size(min = 6, max = 6) String gameCode) {
        try {

            GameSessionDTO gameSession = gameService.getGameSessionDTO(gameCode);
            return ResponseEntity.ok(gameSession);
        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame(@Valid @RequestBody CreateGameRequest request) {

        try {
            UUID userId = authenticationUtil.getCurrentUserId();

            String gameCode = gameService.createGame(
                    userId,
                    request.quizId(),
                    request.questionTimeLimit());
            return ResponseEntity.ok(gameCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<Void> joinGame(@NotNull @PathVariable UUID id) {

        try {
            UUID userId = authenticationUtil.getCurrentUserId();
            gameService.joinGame(userId, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<Void> startGame(@NotNull @PathVariable UUID id) {

        try {
            UUID userId = authenticationUtil.getCurrentUserId();

            gameService.startGame(userId, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{gameSessionId}/is-host")
    public ResponseEntity<Boolean> isHost(@NotNull @PathVariable UUID gameSessionId) {
        UUID userId = authenticationUtil.getCurrentUserId();

        return ResponseEntity.ok(this.gameService.isHost(userId, gameSessionId));
    }

    @PostMapping("/answer")
    public ResponseEntity<Void> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {

        UUID userId = authenticationUtil.getCurrentUserId();

        try {
            gameService.submitAnswer(
                    userId,
                    request.gameSessionId(),
                    request.questionId(),
                    request.answer(), request.answerTime());
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/next/{id}")
    public ResponseEntity<Void> nextQuestion(@NotNull @PathVariable UUID id) {

        UUID userId = authenticationUtil.getCurrentUserId();

        try {
            gameService.nextQuestion(userId, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/emoji")
    public ResponseEntity<Void> sendEmoji(@NotNull @PathVariable UUID id, @NotBlank @RequestBody String emoji) {
        try {
            gameService.sendEmoji(id, emoji);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}