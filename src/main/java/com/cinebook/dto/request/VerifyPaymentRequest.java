
        package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyPaymentRequest(
        Long bookingId,
        @NotBlank
        String razorpayOrderId,

        @NotBlank
        String razorpayPaymentId,

        @NotBlank
        String razorpaySignature

) {
}

