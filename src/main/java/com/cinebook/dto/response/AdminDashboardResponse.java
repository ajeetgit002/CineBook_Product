package com.cinebook.dto.response;

public record AdminDashboardResponse(
        long users,
        long movies,
        long theatres,
        long shows,
        long bookings,
        long offers
) {
}
