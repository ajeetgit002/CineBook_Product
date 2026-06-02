package com.cinebook.dto.response;

import com.cinebook.entity.Movie;
import com.cinebook.enums.MovieStatus;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        String language,
        String genre,
        String format,
        LocalDate releaseDate,
        Double rating,
        String posterUrl,
        String bannerUrl,
        String trailerUrl,
        MovieStatus status
) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getLanguage(),
                movie.getGenre(),
                movie.getFormat(),
                movie.getReleaseDate(),
                movie.getRating(),
                movie.getPosterUrl(),
                movie.getBannerUrl(),
                movie.getTrailerUrl(),
                movie.getStatus()
        );
    }
}
