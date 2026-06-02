package com.cinebook.dto.request;

import com.cinebook.enums.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank String title,
        String description,
        @NotNull Integer durationMinutes,
        @NotBlank String language,
        String genre,
        String format,
        LocalDate releaseDate,
        Double rating,
        String posterUrl,
        String bannerUrl,
        String trailerUrl,
        MovieStatus status
) {
}
