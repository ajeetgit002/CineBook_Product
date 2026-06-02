package com.cinebook.dto.response;

import com.cinebook.entity.Theatre;

public record TheatreResponse(
        Long id,
        String name,
        String address,
        String city,
        String area,
        String format,
        Double rating,
        Boolean active
) {
    public static TheatreResponse from(Theatre theatre) {
        return new TheatreResponse(
                theatre.getId(),
                theatre.getName(),
                theatre.getAddress(),
                theatre.getCity(),
                theatre.getArea(),
                theatre.getFormat(),
                theatre.getRating(),
                theatre.getActive()
        );
    }
}
