package com.kamehoot.kamehoot_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.kamehoot.kamehoot_backend.websocket.GameWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameWebSocketHandler webSocketHandler;

    private final String frontendAddress;

    private final String frontendPort;

    public WebSocketConfig(GameWebSocketHandler webSocketHandler, @Value("${frontend.address}") String frontendAddress,
            @Value("${frontend.port}") String frontendPort) {
        this.webSocketHandler = webSocketHandler;
        this.frontendAddress = frontendAddress;
        this.frontendPort = frontendPort;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(webSocketHandler, "/game")
                .setAllowedOrigins("http://" + this.frontendAddress + ":" + this.frontendPort);
    }

}
