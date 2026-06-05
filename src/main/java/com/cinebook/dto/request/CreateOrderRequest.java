
        package com.cinebook.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(

        @NotNull
        @Positive
        Long bookingId,

        @NotNull
        @Positive
        Double amount

) {
}

