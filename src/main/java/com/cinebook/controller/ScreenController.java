package com.cinebook.controller;

import com.cinebook.dto.request.ScreenRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.ScreenResponse;
import com.cinebook.service.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping("/admin/screens")
    public ApiResponse<ScreenResponse> createScreen(@Valid @RequestBody ScreenRequest request) {
        log.info("[ScreenController] Received request to create screen: {}", request.name());
        return ApiResponse.success("Screen created successfully", screenService.create(request));
    }

    @GetMapping("/screens/{id}")
    public ApiResponse<ScreenResponse> getScreen(@PathVariable Long id) {
        log.debug("[ScreenController] Received request to fetch screen with id: {}", id);
        return ApiResponse.success("Screen fetched successfully", screenService.getScreen(id));
    }

    @GetMapping("/screens")
    public ApiResponse<List<ScreenResponse>> getScreens(
            @RequestParam(required = false) Long theatreId) {
        log.debug("[ScreenController] Received request to fetch screens. theatreId: {}", theatreId);
        return ApiResponse.success("Screens fetched successfully", screenService.getScreens(theatreId));
    }

    @DeleteMapping("/admin/screens/{id}")
    public ApiResponse<Void> deleteScreen(@PathVariable Long id) {
        log.info("[ScreenController] Received request to delete screen with id: {}", id);
        screenService.delete(id);
        return ApiResponse.success("Screen deleted successfully", null);
    }
}