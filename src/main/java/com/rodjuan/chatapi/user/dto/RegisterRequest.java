package com.rodjuan.chatapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to register a user")
public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Schema(description = "User name", example = "Maria")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "The email is not valid")
        @Schema(description = "User e-mail", example = "maria@example.com", format = "email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "The password must have at least 6 characters")
        @Schema(description = "User password", example = "password123", format = "password", minLength = 6)
        String password
) {
}
