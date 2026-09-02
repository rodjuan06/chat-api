package com.rodjuan.chatapi.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Message returned by the API")
public record MessageResponse(

        @Schema(description = "Message identifier", example = "66d7703b701a5c46272c2210")
        String id,

        @Schema(description = "Chat identifier", example = "66d7703b701a5c46272c220f")
        String chatId,

        @Schema(description = "ID of the user who sent the message", example = "1")
        long senderId,

        @Schema(description = "Message text", example = "Hello")
        String text,

        @Schema(description = "Optional image encoded as Base64", format = "byte")
        byte[] image,

        @Schema(description = "Date and time when the message was sent", example = "2026-09-02T17:30:00")
        LocalDateTime date
) {
}
