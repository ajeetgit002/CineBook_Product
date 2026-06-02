package com.cinebook.dto.response;

public record TicketResponse(
        BookingResponse booking,
        String qrPayload
) {
}
