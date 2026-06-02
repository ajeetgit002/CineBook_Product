package com.cinebook.service;

import com.cinebook.dto.request.TheatreRequest;
import com.cinebook.dto.response.ShowResponse;
import com.cinebook.dto.response.TheatreResponse;

import java.util.List;

public interface TheatreService {
    List<TheatreResponse> getTheatres();

    TheatreResponse getTheatre(Long theatreId);

    List<ShowResponse> getShows(Long theatreId);

    List<String> getFilters();

    List<TheatreResponse> search(String keyword);

    List<TheatreResponse> filter(String city, String area, String format);

    List<TheatreResponse> popular();

    List<TheatreResponse> recommended();

    TheatreResponse create(TheatreRequest request);

    TheatreResponse update(Long id, TheatreRequest request);

    void delete(Long id);
}
