package com.cinebook.dto.response;

import com.cinebook.entity.Show;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShowResponse(
        Long id,
        Long movieId,
        String movieTitle,
        Long theatreId,
        String theatreName,
        Long screenId,
        String screenName,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal price
) {
    public static ShowResponse from(Show show) {
        Long theatreId = show.getScreen() == null || show.getScreen().getTheatre() == null
                ? null
                : show.getScreen().getTheatre().getId();
        String theatreName = show.getScreen() == null || show.getScreen().getTheatre() == null
                ? null
                : show.getScreen().getTheatre().getName();
        return new ShowResponse(
                show.getId(),
                show.getMovie() == null ? null : show.getMovie().getId(),
                show.getMovie() == null ? null : show.getMovie().getTitle(),
                theatreId,
                theatreName,
                show.getScreen() == null ? null : show.getScreen().getId(),
                show.getScreen() == null ? null : show.getScreen().getName(),
                show.getShowDate(),
                show.getStartTime(),
                show.getEndTime(),
                show.getPrice()
        );
    }
}
