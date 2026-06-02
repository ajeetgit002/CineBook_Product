package com.cinebook.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShowRequest(
        @NotNull Long movieId,
        @NotNull Long screenId,
        @NotNull LocalDate showDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull BigDecimal price
) {
}
