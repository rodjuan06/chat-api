package com.rodjuan.chatapi.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Chat returned by the API")
public record ChatResponse(

        @Schema(description = "Chat identifier", example = "66d7703b701a5c46272c2210")
        String id,

        @Schema(description = "Chat name", example = "General")
        String name,

        @Schema(description = "IDs of the users included in the chat", example = "[1, 2]")
        List<Long> memberIds,

        @Schema(description = "Date and time when the chat was created", example = "2026-09-02T17:30:00")
        LocalDateTime createdAt
) {
}
