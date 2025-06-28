package com.kamehoot.kamehoot_backend.DTOs;

import com.kamehoot.kamehoot_backend.enums.GameStatus;

public record WebSocketDTO(
                String type,
                GameStatus gameSessionStatus,
                Object info) {

}
