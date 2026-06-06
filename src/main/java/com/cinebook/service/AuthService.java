package com.cinebook.service;

import com.cinebook.dto.request.*;
import com.cinebook.dto.response.AuthResponse;
import com.cinebook.dto.response.OtpResponse;
import com.cinebook.dto.response.RegisterResponse;

public interface AuthService {


    AuthResponse login(LoginRequest request);

    String logout(String refreshToken);

    AuthResponse refreshToken(TokenRefreshRequest request);

    String forgotPassword(ForgotPasswordRequest request);

    String resetPassword(ResetPasswordRequest request);


    OtpResponse register(RegisterRequest request);
    void verifyOtp(
            VerifyOtpRequest request);

    void resendOtp(
            ResendOtpRequest request);
}
