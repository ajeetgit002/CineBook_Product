package com.cinebook.dto.response;

import com.cinebook.entity.Seat;
import com.cinebook.enums.SeatStatus;
import com.cinebook.enums.SeatType;

import java.math.BigDecimal;

public record SeatResponse(
        Long id,
        String seatNumber,
        SeatType seatType,
        BigDecimal basePrice,
        SeatStatus status
) {
    public static SeatResponse from(Seat seat,  SeatStatus status ){
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatType(),
                seat.getBasePrice(),
                SeatStatus.valueOf(status.name())
        );
    }
}
