package com.cinebook.dto.response;

import com.cinebook.entity.SupportTicket;
import com.cinebook.enums.SupportStatus;

import java.time.LocalDateTime;

public record SupportTicketResponse(
        Long id,
        String subject,
        String message,
        SupportStatus status,
        LocalDateTime createdAt
) {
    public static SupportTicketResponse from(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getMessage(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );
    }
}
