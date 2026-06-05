
        package com.cinebook.dto.response;

import com.cinebook.enums.SeatStatus;

public record SeatUpdateEvent(

        Long showId,
        String seatNumber,
        SeatStatus status

) {
}

