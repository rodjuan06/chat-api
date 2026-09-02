package com.rodjuan.chatapi.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Data required to create a chat")
public record ChatCreateRequest(

        @NotBlank(message = "Chat name is required")
        @Schema(description = "Chat name", example = "General")
        String name,

        @Schema(description = "IDs of the users included in the chat", example = "[1, 2]")
        List<Long> memberIds
) {
}
