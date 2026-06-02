package com.cinebook.controller;

import com.cinebook.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/others")
public class OthersController {

    @GetMapping("/general")
    public ApiResponse<Map<String, Object>> general() {
        return ApiResponse.success("General details fetched successfully", Map.of(
                "appName", "CineBook",
                "version", "1.0.0",
                "supportEmail", "support@cinebook.com",
                "termsUrl", "/terms",
                "privacyUrl", "/privacy"
        ));
    }
}
