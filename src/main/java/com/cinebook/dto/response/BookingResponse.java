package com.cinebook.dto.response;

import com.cinebook.entity.Booking;
import com.cinebook.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record BookingResponse(
        Long id,
        String bookingCode,
        Integer ticketCount,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime bookingTime,
        String movieTitle,
        String theatreName,
        LocalDate showDate,
        LocalTime startTime,
        List<String> seats
) {
    public static BookingResponse from(Booking booking, List<String> seats) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingCode(),
                booking.getTicketCount(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getBookingTime(),
                booking.getShow().getMovie().getTitle(),
                booking.getShow().getScreen().getTheatre().getName(),
                booking.getShow().getShowDate(),
                booking.getShow().getStartTime(),
                seats
        );
    }
}
