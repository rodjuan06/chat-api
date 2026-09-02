package com.rodjuan.chatapi.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data allowed when updating a chat")
public record ChatUpdateRequest(

        @NotBlank(message = "Chat name is required")
        @Schema(description = "New chat name", example = "Development team")
        String name
) {
}
