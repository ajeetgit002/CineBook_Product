package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PaymentMethodRequest(
        @NotBlank String provider,
        @NotBlank String type,
        @NotBlank String maskedNumber,
        Boolean defaultMethod
) {
}
