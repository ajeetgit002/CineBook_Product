package com.cinebook.dto.response;

import com.cinebook.entity.Seat;
import com.cinebook.enums.SeatType;

import java.math.BigDecimal;

public record SeatResponse(
        Long id,
        String seatNumber,
        SeatType seatType,
        BigDecimal basePrice,
        boolean booked
) {
    public static SeatResponse from(Seat seat, boolean booked) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatType(),
                seat.getBasePrice(),
                booked
        );
    }
}
