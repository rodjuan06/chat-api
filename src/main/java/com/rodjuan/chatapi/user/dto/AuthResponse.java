package com.rodjuan.chatapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication result")
public record AuthResponse(
        @Schema(description = "JWT used to access protected endpoints", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}
