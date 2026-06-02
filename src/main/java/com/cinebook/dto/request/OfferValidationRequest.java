package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OfferValidationRequest(@NotBlank String couponCode) {
}
