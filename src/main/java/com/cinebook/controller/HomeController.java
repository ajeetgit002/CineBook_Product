package com.cinebook.controller;

import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.HomeResponse;
import com.cinebook.dto.response.MovieResponse;
import com.cinebook.service.MovieService;
import com.cinebook.service.OfferService;
import com.cinebook.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final MovieService movieService;
    private final TheatreService theatreService;
    private final OfferService offerService;

    @GetMapping
    public ApiResponse<HomeResponse> home() {
        HomeResponse response = new HomeResponse(
                movieService.nowShowing().stream().limit(5).toList(),
                movieService.nowShowing(),
                movieService.upcoming(),
                theatreService.recommended(),
                offerService.all(),
                movieService.getFilters()
        );
        return ApiResponse.success("Home fetched successfully", response);
    }

    @GetMapping("/search")
    public ApiResponse<List<MovieResponse>> search(@RequestParam("q") String keyword) {
        return ApiResponse.success("Home search completed successfully", movieService.search(keyword));
    }
}
