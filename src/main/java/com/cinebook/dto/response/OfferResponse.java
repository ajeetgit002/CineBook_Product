package com.cinebook.dto.response;

import com.cinebook.entity.Offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferResponse(
        Long id,
        String title,
        String description,
        String category,
        String couponCode,
        BigDecimal discountValue,
        LocalDate validFrom,
        LocalDate validTo,
        Boolean active
) {
    public static OfferResponse from(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getCategory(),
                offer.getCouponCode(),
                offer.getDiscountValue(),
                offer.getValidFrom(),
                offer.getValidTo(),
                offer.getActive()
        );
    }
}
