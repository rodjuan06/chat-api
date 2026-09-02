package com.rodjuan.chatapi.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to send a message")
public record MessageCreateRequest(

        @NotBlank(message = "Message text is required")
        @Size(max = 5000, message = "Message text is too long")
        @Schema(description = "Message text", example = "Hello", maxLength = 5000)
        String text,

        @Schema(description = "Optional image encoded as Base64", format = "byte")
        byte[] image
) {
}
