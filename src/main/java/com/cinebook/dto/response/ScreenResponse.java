package com.cinebook.dto.response;

import com.cinebook.entity.Screen;
import com.cinebook.enums.ScreenType;

public record ScreenResponse(
        Long id,
        String name,
        Integer capacity,
        ScreenType screenType,
        Long theatreId
) {
    public static ScreenResponse from(Screen screen) {
        return new ScreenResponse(
                screen.getId(),
                screen.getName(),
                screen.getCapacity(),
                screen.getScreenType(),
                screen.getTheatre() != null ? screen.getTheatre().getId() : null
        );
    }
}
