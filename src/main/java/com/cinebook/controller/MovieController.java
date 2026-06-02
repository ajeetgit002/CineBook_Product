package com.cinebook.controller;

import com.cinebook.dto.response.*;
import com.cinebook.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ApiResponse<List<MovieResponse>> movies() {
        return ApiResponse.success("Movies fetched successfully", movieService.getMovies());
    }

    @GetMapping("/filters")
    public ApiResponse<List<String>> filters() {
        return ApiResponse.success("Movie filters fetched successfully", movieService.getFilters());
    }

    @GetMapping("/search")
    public ApiResponse<List<MovieResponse>> search(@RequestParam("q") String keyword) {
        return ApiResponse.success("Movies searched successfully", movieService.search(keyword));
    }

    @GetMapping("/filter")
    public ApiResponse<List<MovieResponse>> filter(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String format) {
        return ApiResponse.success("Movies filtered successfully", movieService.filter(language, genre, format));
    }

    @GetMapping("/top-rated")
    public ApiResponse<List<MovieResponse>> topRated() {
        return ApiResponse.success("Top rated movies fetched successfully", movieService.topRated());
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<MovieResponse>> upcoming() {
        return ApiResponse.success("Upcoming movies fetched successfully", movieService.upcoming());
    }

    @GetMapping("/now-showing")
    public ApiResponse<List<MovieResponse>> nowShowing() {
        return ApiResponse.success("Now showing movies fetched successfully", movieService.nowShowing());
    }

    @GetMapping("/{movieId}")
    public ApiResponse<MovieResponse> movie(@PathVariable Long movieId) {
        return ApiResponse.success("Movie fetched successfully", movieService.getMovie(movieId));
    }

    @GetMapping("/{movieId}/cast")
    public ApiResponse<List<MovieCastResponse>> cast(@PathVariable Long movieId) {
        return ApiResponse.success("Movie cast fetched successfully", movieService.getCast(movieId));
    }

    @GetMapping("/{movieId}/reviews")
    public ApiResponse<List<MovieReviewResponse>> reviews(@PathVariable Long movieId) {
        return ApiResponse.success("Movie reviews fetched successfully", movieService.getReviews(movieId));
    }

    @GetMapping("/{movieId}/recommended")
    public ApiResponse<List<MovieResponse>> recommended(@PathVariable Long movieId) {
        return ApiResponse.success("Recommended movies fetched successfully", movieService.getRecommended(movieId));
    }
}
