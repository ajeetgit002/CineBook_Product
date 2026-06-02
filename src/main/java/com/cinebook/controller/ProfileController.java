package com.cinebook.controller;

import com.cinebook.dto.request.*;
import com.cinebook.dto.response.*;
import com.cinebook.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<ProfileResponse> profile() {
        return ApiResponse.success("Profile fetched successfully", userService.profile());
    }

    @PutMapping
    public ApiResponse<ProfileResponse> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return ApiResponse.success("Profile updated successfully", userService.updateProfile(request));
    }

    @PostMapping("/image")
    public ApiResponse<ProfileResponse> image(@RequestParam String imageUrl) {
        return ApiResponse.success("Profile image updated successfully", userService.updateImage(imageUrl));
    }

    @GetMapping("/rewards")
    public ApiResponse<Map<String, Object>> rewards() {
        return ApiResponse.success("Rewards fetched successfully", userService.rewards());
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.success("Profile overview fetched successfully", userService.overview());
    }

    @GetMapping("/payment-methods")
    public ApiResponse<List<PaymentMethodResponse>> paymentMethods() {
        return ApiResponse.success("Payment methods fetched successfully", userService.paymentMethods());
    }

    @PostMapping("/payment-methods")
    public ApiResponse<PaymentMethodResponse> addPaymentMethod(@Valid @RequestBody PaymentMethodRequest request) {
        return ApiResponse.success("Payment method added successfully", userService.addPaymentMethod(request));
    }

    @DeleteMapping("/payment-methods/{id}")
    public ApiResponse<Void> deletePaymentMethod(@PathVariable Long id) {
        userService.deletePaymentMethod(id);
        return ApiResponse.success("Payment method deleted successfully", null);
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ApiResponse.success("Password changed successfully", userService.changePassword(request));
    }

    @PutMapping("/change-email")
    public ApiResponse<ProfileResponse> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        return ApiResponse.success("Email changed successfully", userService.changeEmail(request));
    }

    @PutMapping("/change-phone")
    public ApiResponse<ProfileResponse> changePhone(@Valid @RequestBody ChangePhoneRequest request) {
        return ApiResponse.success("Phone changed successfully", userService.changePhone(request));
    }
}
