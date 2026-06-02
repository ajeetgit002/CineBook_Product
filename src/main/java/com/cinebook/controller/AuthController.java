package com.cinebook.controller;

import com.cinebook.dto.request.*;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.AuthResponse;
import com.cinebook.dto.response.RegisterResponse;
import com.cinebook.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("User registered successfully", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }


    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestBody TokenRefreshRequest request) {

        return ApiResponse.success(
                "Logout successful",
                authService.logout(request.refreshToken())
        );
    }


    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.success("Token refreshed successfully", authService.refreshToken(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ApiResponse.success("Forgot password request accepted", authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ApiResponse.success("Password reset successfully", authService.resetPassword(request));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponse.success("OTP verified successfully", authService.verifyOtp(request));
    }
}
