package com.kamehoot.kamehoot_backend.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.kamehoot.kamehoot_backend.services.IUserService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final IGameService gameService;
    private final IUserService userService;

    public GameController(IGameService gameService, IUserService userService) {
        this.gameService = gameService;
        this.userService = userService;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {

            return ResponseEntity.badRequest().build();

        }
        try {
            String gameCode = gameService.createGame(
                    authenticatedUserId,
                    request.quizId(),
                    request.questionTimeLimit());
            return ResponseEntity.ok(gameCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<Void> joinGame(@NotNull @PathVariable UUID id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {

            return ResponseEntity.badRequest().build();

        }
        try {
            gameService.joinGame(authenticatedUserId, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<Void> startGame(@NotNull @PathVariable UUID id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {
            return ResponseEntity.badRequest().build();

        }
        try {
            gameService.startGame(authenticatedUserId, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{gameSessionId}/is-host")
    public ResponseEntity<Boolean> isHost(@NotNull @PathVariable UUID gameSessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {

            return ResponseEntity.badRequest().build();

        }

        return ResponseEntity.ok(this.gameService.isHost(authenticatedUserId, gameSessionId));
    }

    @PostMapping("/answer")
    public ResponseEntity<Void> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {

            return ResponseEntity.badRequest().build();

        }

        try {
            gameService.submitAnswer(
                    authenticatedUserId,
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {

            return ResponseEntity.badRequest().build();

        }
        try {
            gameService.nextQuestion(authenticatedUserId, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/emoji")
    public ResponseEntity<Void> sendEmoji(@NotNull @PathVariable UUID id, @NotBlank @RequestBody String emoji) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = null;
        if (auth instanceof UsernamePasswordAuthenticationToken jwtAuth) {

            String username = jwtAuth.getName();
            try {
                authenticatedUserId = this.userService.getUserByUsername(username).getId();

            } catch (Exception e) {
                // If user not found, continue with null (public questions only)
                return ResponseEntity.badRequest().build();
            }

        }
        if (authenticatedUserId == null) {

            return ResponseEntity.badRequest().build();

        }
        try {
            gameService.sendEmoji(id, emoji);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}