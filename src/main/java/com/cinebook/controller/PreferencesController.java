package com.cinebook.controller;

import com.cinebook.dto.request.*;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.PreferenceResponse;
import com.cinebook.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<PreferenceResponse> preferences() {
        return ApiResponse.success("Preferences fetched successfully", userService.preferences());
    }

    @PutMapping
    public ApiResponse<PreferenceResponse> updatePreferences(@RequestBody PreferenceRequest request) {
        return ApiResponse.success("Preferences updated successfully", userService.updatePreferences(request));
    }

    @GetMapping("/notifications")
    public ApiResponse<PreferenceResponse> notifications() {
        return ApiResponse.success("Notification preferences fetched successfully", userService.notifications());
    }

    @PutMapping("/notifications")
    public ApiResponse<PreferenceResponse> updateNotifications(@RequestBody NotificationPreferenceRequest request) {
        return ApiResponse.success("Notification preferences updated successfully", userService.updateNotifications(request));
    }

    @GetMapping("/languages")
    public ApiResponse<List<String>> languages() {
        return ApiResponse.success("Languages fetched successfully", userService.languages());
    }

    @PutMapping("/language")
    public ApiResponse<PreferenceResponse> updateLanguage(@Valid @RequestBody LanguagePreferenceRequest request) {
        return ApiResponse.success("Language preference updated successfully", userService.updateLanguage(request));
    }

    @PutMapping("/theme")
    public ApiResponse<PreferenceResponse> updateTheme(@Valid @RequestBody ThemePreferenceRequest request) {
        return ApiResponse.success("Theme preference updated successfully", userService.updateTheme(request));
    }
}
