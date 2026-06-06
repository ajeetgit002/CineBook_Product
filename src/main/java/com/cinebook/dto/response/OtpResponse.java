package com.cinebook.dto.response;


public record OtpResponse(
        String email,
        String expiresIn
) {
}