package com.cinebook.service;

import com.cinebook.dto.request.ShowRequest;
import com.cinebook.dto.response.SeatResponse;
import com.cinebook.dto.response.ShowResponse;

import java.util.List;

public interface ShowService {
    List<ShowResponse> getByMovie(Long movieId);

    List<ShowResponse> getByTheatre(Long theatreId);

    ShowResponse getShow(Long showId);

    List<SeatResponse> getSeats(Long showId);

    ShowResponse create(ShowRequest request);

    ShowResponse update(Long id, ShowRequest request);

    void delete(Long id);
}
