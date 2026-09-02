package com.rodjuan.chatapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User authentication credentials")
public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "The email is not valid")
        @Schema(description = "User e-mail", example = "maria@example.com", format = "email")
        String email,

        @NotBlank(message = "Password is required")
        @Schema(description = "User password", example = "password123", format = "password")
        String password
) {
}
