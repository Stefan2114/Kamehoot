package com.kamehoot.kamehoot_backend.websocket;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.kamehoot.kamehoot_backend.services.IGameService;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final IGameService gameService;

    public GameWebSocketHandler(IGameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String query = session.getUri().getQuery();
        UUID gameSessionId = null;
        if (query != null && query.contains("gameSessionId=")) {

            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("gameSessionId=")) {
                    String idString = param.substring("gameSessionId=".length());
                    gameSessionId = UUID.fromString(idString);
                    break;
                }
            }
        }

        if (gameSessionId == null) {
            throw new Exception("No gameCode found");
        }

        // needs to be changed
        this.gameService.connect(gameSessionId, session);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.gameService.disconnect(session);

    }

}
