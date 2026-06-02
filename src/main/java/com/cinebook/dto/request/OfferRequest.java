package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferRequest(
        @NotBlank String title,
        String description,
        @NotBlank String category,
        @NotBlank String couponCode,
        BigDecimal discountValue,
        LocalDate validFrom,
        LocalDate validTo,
        Boolean active
) {
}
