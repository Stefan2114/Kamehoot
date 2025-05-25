package com.kamehoot.kamehoot_backend.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuizRequest(String title, LocalDateTime creationDate, List<UUID> questionIds) {

}
