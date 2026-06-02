package com.cinebook.dto.response;

import com.cinebook.entity.MovieCast;

public record MovieCastResponse(
        Long id,
        String name,
        String roleName,
        String imageUrl
) {
    public static MovieCastResponse from(MovieCast cast) {
        return new MovieCastResponse(cast.getId(), cast.getName(), cast.getRoleName(), cast.getImageUrl());
    }
}
