package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SupportTicketRequest(
        @NotBlank String subject,
        @NotBlank String message
) {
}
