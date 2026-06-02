package com.cinebook.controller;

import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.ShowResponse;
import com.cinebook.dto.response.TheatreResponse;
import com.cinebook.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @GetMapping
    public ApiResponse<List<TheatreResponse>> theatres() {
        return ApiResponse.success("Theatres fetched successfully", theatreService.getTheatres());
    }

    @GetMapping("/filters")
    public ApiResponse<List<String>> filters() {
        return ApiResponse.success("Theatre filters fetched successfully", theatreService.getFilters());
    }

    @GetMapping("/search")
    public ApiResponse<List<TheatreResponse>> search(@RequestParam("q") String keyword) {
        return ApiResponse.success("Theatres searched successfully", theatreService.search(keyword));
    }

    @GetMapping("/filter")
    public ApiResponse<List<TheatreResponse>> filter(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String format) {
        return ApiResponse.success("Theatres filtered successfully", theatreService.filter(city, area, format));
    }

    @GetMapping("/popular")
    public ApiResponse<List<TheatreResponse>> popular() {
        return ApiResponse.success("Popular theatres fetched successfully", theatreService.popular());
    }

    @GetMapping("/recommended")
    public ApiResponse<List<TheatreResponse>> recommended() {
        return ApiResponse.success("Recommended theatres fetched successfully", theatreService.recommended());
    }

    @GetMapping("/{theatreId}")
    public ApiResponse<TheatreResponse> theatre(@PathVariable Long theatreId) {
        return ApiResponse.success("Theatre fetched successfully", theatreService.getTheatre(theatreId));
    }

    @GetMapping("/{theatreId}/shows")
    public ApiResponse<List<ShowResponse>> shows(@PathVariable Long theatreId) {
        return ApiResponse.success("Theatre shows fetched successfully", theatreService.getShows(theatreId));
    }
}
