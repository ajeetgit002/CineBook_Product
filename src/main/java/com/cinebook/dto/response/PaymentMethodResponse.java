package com.cinebook.dto.response;

import com.cinebook.entity.UserPaymentMethod;

public record PaymentMethodResponse(
        Long id,
        String provider,
        String type,
        String maskedNumber,
        Boolean defaultMethod
) {
    public static PaymentMethodResponse from(UserPaymentMethod paymentMethod) {
        return new PaymentMethodResponse(
                paymentMethod.getId(),
                paymentMethod.getProvider(),
                paymentMethod.getType(),
                paymentMethod.getMaskedNumber(),
                paymentMethod.getDefaultMethod()
        );
    }
}
