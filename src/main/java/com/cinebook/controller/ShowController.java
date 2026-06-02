package com.cinebook.controller;

import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.SeatResponse;
import com.cinebook.dto.response.ShowResponse;
import com.cinebook.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping("/movie/{movieId}")
    public ApiResponse<List<ShowResponse>> byMovie(@PathVariable Long movieId) {
        return ApiResponse.success("Movie shows fetched successfully", showService.getByMovie(movieId));
    }

    @GetMapping("/theatre/{theatreId}")
    public ApiResponse<List<ShowResponse>> byTheatre(@PathVariable Long theatreId) {
        return ApiResponse.success("Theatre shows fetched successfully", showService.getByTheatre(theatreId));
    }

    @GetMapping("/{showId}")
    public ApiResponse<ShowResponse> show(@PathVariable Long showId) {
        return ApiResponse.success("Show fetched successfully", showService.getShow(showId));
    }

    @GetMapping("/{showId}/seats")
    public ApiResponse<List<SeatResponse>> seats(@PathVariable Long showId) {
        return ApiResponse.success("Show seats fetched successfully", showService.getSeats(showId));
    }
}
