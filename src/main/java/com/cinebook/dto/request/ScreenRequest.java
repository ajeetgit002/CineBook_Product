package com.cinebook.dto.request;

import com.cinebook.enums.ScreenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ScreenRequest(
        @NotNull @Positive Long theatreId,
        @NotBlank String name,
        @NotNull @Positive Integer capacity,
        @NotNull ScreenType screenType
) {
}
