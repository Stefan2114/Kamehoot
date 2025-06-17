package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.UUID;

public record GameEventMessage(GameEventType type,
        UUID gameSessionId,
        String gameCode,
        Object data,
        LocalDateTime timestamp) {

}
