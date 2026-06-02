package com.cinebook.controller;

import com.cinebook.dto.request.MovieRequest;
import com.cinebook.dto.request.OfferRequest;
import com.cinebook.dto.request.ShowRequest;
import com.cinebook.dto.request.TheatreRequest;
import com.cinebook.dto.response.*;
import com.cinebook.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MovieService movieService;
    private final TheatreService theatreService;
    private final ShowService showService;
    private final OfferService offerService;
    private final AdminService adminService;

    @PostMapping("/movies")
    public ApiResponse<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return ApiResponse.success("Movie created successfully", movieService.create(request));
    }

    @PutMapping("/movies/{id}")
    public ApiResponse<MovieResponse> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return ApiResponse.success("Movie updated successfully", movieService.update(id, request));
    }

    @DeleteMapping("/movies/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
        return ApiResponse.success("Movie deleted successfully", null);
    }

    @PostMapping("/theatres")
    public ApiResponse<TheatreResponse> createTheatre(@Valid @RequestBody TheatreRequest request) {
        return ApiResponse.success("Theatre created successfully", theatreService.create(request));
    }

    @PutMapping("/theatres/{id}")
    public ApiResponse<TheatreResponse> updateTheatre(@PathVariable Long id, @Valid @RequestBody TheatreRequest request) {
        return ApiResponse.success("Theatre updated successfully", theatreService.update(id, request));
    }

    @DeleteMapping("/theatres/{id}")
    public ApiResponse<Void> deleteTheatre(@PathVariable Long id) {
        theatreService.delete(id);
        return ApiResponse.success("Theatre deleted successfully", null);
    }

    @PostMapping("/shows")
    public ApiResponse<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        return ApiResponse.success("Show created successfully", showService.create(request));
    }

    @PutMapping("/shows/{id}")
    public ApiResponse<ShowResponse> updateShow(@PathVariable Long id, @Valid @RequestBody ShowRequest request) {
        return ApiResponse.success("Show updated successfully", showService.update(id, request));
    }

    @DeleteMapping("/shows/{id}")
    public ApiResponse<Void> deleteShow(@PathVariable Long id) {
        showService.delete(id);
        return ApiResponse.success("Show deleted successfully", null);
    }

    @PostMapping("/offers")
    public ApiResponse<OfferResponse> createOffer(@Valid @RequestBody OfferRequest request) {
        return ApiResponse.success("Offer created successfully", offerService.create(request));
    }

    @PutMapping("/offers/{id}")
    public ApiResponse<OfferResponse> updateOffer(@PathVariable Long id, @Valid @RequestBody OfferRequest request) {
        return ApiResponse.success("Offer updated successfully", offerService.update(id, request));
    }

    @DeleteMapping("/offers/{id}")
    public ApiResponse<Void> deleteOffer(@PathVariable Long id) {
        offerService.delete(id);
        return ApiResponse.success("Offer deleted successfully", null);
    }

    @GetMapping("/users")
    public ApiResponse<List<UserSummaryResponse>> users() {
        return ApiResponse.success("Users fetched successfully", adminService.users());
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserSummaryResponse> user(@PathVariable Long id) {
        return ApiResponse.success("User fetched successfully", adminService.user(id));
    }

    @PutMapping("/users/{id}/block")
    public ApiResponse<UserSummaryResponse> block(@PathVariable Long id) {
        return ApiResponse.success("User blocked successfully", adminService.block(id));
    }

    @PutMapping("/users/{id}/unblock")
    public ApiResponse<UserSummaryResponse> unblock(@PathVariable Long id) {
        return ApiResponse.success("User unblocked successfully", adminService.unblock(id));
    }

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> dashboard() {
        return ApiResponse.success("Admin dashboard fetched successfully", adminService.dashboard());
    }
}
