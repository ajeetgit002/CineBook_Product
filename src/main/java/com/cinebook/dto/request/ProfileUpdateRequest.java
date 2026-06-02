package com.cinebook.dto.request;

public record ProfileUpdateRequest(
        String firstName,
        String lastName,
        String phone
) {
}
