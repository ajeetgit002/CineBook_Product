package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TheatreRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String city,
        String area,
        String format,
        Double rating,
        Boolean active
) {
}
