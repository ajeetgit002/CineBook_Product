package com.cinebook.dto.response;

import java.util.List;

public record HomeResponse(
        List<MovieResponse> bannerCarousel,
        List<MovieResponse> nowShowing,
        List<MovieResponse> upcomingMovies,
        List<TheatreResponse> recommendedTheatres,
        List<OfferResponse> offers,
        List<String> genres
) {
}
