package com.cinebook.dto.response;

public record AuthResponse(
        Long userId,
        String email,
        String role,
        String accessToken,
        String refreshToken
) {
}
