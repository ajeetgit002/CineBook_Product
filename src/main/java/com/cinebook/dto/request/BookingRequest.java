package com.cinebook.dto.request;

import com.cinebook.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull Long showId,
        @NotEmpty List<Long> seatIds,
        PaymentMethod paymentMethod
) {
}
