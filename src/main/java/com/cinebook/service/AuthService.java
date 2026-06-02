package com.cinebook.service;

import com.cinebook.dto.request.ForgotPasswordRequest;
import com.cinebook.dto.request.LoginRequest;
import com.cinebook.dto.request.RegisterRequest;
import com.cinebook.dto.request.ResetPasswordRequest;
import com.cinebook.dto.request.TokenRefreshRequest;
import com.cinebook.dto.request.VerifyOtpRequest;
import com.cinebook.dto.response.AuthResponse;
import com.cinebook.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String logout();

    AuthResponse refreshToken(TokenRefreshRequest request);

    String forgotPassword(ForgotPasswordRequest request);

    String resetPassword(ResetPasswordRequest request);

    String verifyOtp(VerifyOtpRequest request);
}
