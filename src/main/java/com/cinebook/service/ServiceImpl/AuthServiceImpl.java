package com.cinebook.service.ServiceImpl;


import com.cinebook.config.OtpGenerator;
import com.cinebook.dto.request.*;
import com.cinebook.dto.response.AuthResponse;
import com.cinebook.dto.response.OtpResponse;
import com.cinebook.dto.response.RegisterResponse;
import com.cinebook.entity.Role;
import com.cinebook.entity.User;
import com.cinebook.exceptions.*;
import com.cinebook.repository.RoleRepository;
import com.cinebook.repository.UserRepository;
import com.cinebook.security.JwtService;
import com.cinebook.service.AuthService;
import com.cinebook.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {



    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final OtpGenerator otpGenerator;

    private final ObjectMapper objectMapper;



    @Override
    @Transactional
    public OtpResponse register(
            RegisterRequest request) {

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new DuplicateResourceException(
                    "Email already registered");
        }

        String otp =
                otpGenerator.generateOtp();

        PendingRegistration pending =
                PendingRegistration.builder()
                        .firstName(
                                request.getFirstName())
                        .lastName(
                                request.getLastName())
                        .email(
                                request.getEmail())
                        .phone(
                                request.getPhone())
                        .password(
                                request.getPassword())
                        .build();

        try {

            String json =
                    objectMapper.writeValueAsString(
                            pending);

            redisTemplate.opsForValue().set(
                    "register:" + request.getEmail(),
                    json,
                    Duration.ofMinutes(5)
            );

            redisTemplate.opsForValue().set(
                    "otp:" + request.getEmail(),
                    otp,
                    Duration.ofMinutes(5)
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to store registration data",
                    e
            );
        }

        emailService.sendOtpEmail(
                request.getEmail(),
                otp
        );

        return new OtpResponse(
                request.getEmail(),
                "5 minutes"
        );
    }

@Override
public AuthResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.email())
            .orElseThrow(() ->
                    new UnauthorizedException(
                            "Invalid email or password"));

    if (Boolean.FALSE.equals(user.getEnabled())) {
        throw new ForbiddenException(
                "User account is blocked");
    }

    if (!passwordEncoder.matches(
            request.password(),
            user.getPassword())) {

        throw new UnauthorizedException(
                "Invalid email or password");
    }

    String accessToken = jwtService.generateToken(user);

    String refreshToken = refreshTokenService
            .createRefreshToken(user)
            .getToken();

    return new AuthResponse(
            user.getId(),
            user.getEmail(),
            user.getRole().getName(),
            accessToken,
            refreshToken
    );
}



    @Override
    public String logout(String refreshToken) {

        refreshTokenService.revokeToken(refreshToken);

        return "Logged out successfully";
    }



    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {

        var refreshToken = refreshTokenService
                .validateRefreshToken(request.refreshToken());

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        refreshTokenService.revokeToken(
                refreshToken.getToken());

        String newRefreshToken =
                refreshTokenService
                        .createRefreshToken(user)
                        .getToken();

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                accessToken,
                newRefreshToken
        );
    }



    @Override
    @Transactional
    public String forgotPassword(
            ForgotPasswordRequest request) {

        User user =
                userRepository.findByEmail(
                        request.email()
                ).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found"
                        )
                );

        String otp =
                otpGenerator.generateOtp();

        redisTemplate.opsForValue().set(
                "forgot-otp:" + user.getEmail(),
                otp,
                Duration.ofMinutes(5)
        );

        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return "Password reset OTP sent successfully";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return "Password reset successfully";
    }



    @Override
    @Transactional
    public void verifyOtp(
            VerifyOtpRequest request) {

        String storedOtp =
                redisTemplate.opsForValue()
                        .get("otp:" + request.email());

        if (storedOtp == null) {
            throw new RuntimeException("OTP expired");
        }

        if (!storedOtp.equals(request.otp())) {
            throw new RuntimeException("Invalid OTP");
        }

        String registrationJson =
                redisTemplate.opsForValue()
                        .get("register:" + request.email());

        PendingRegistration registration;

        try {

            registration =
                    objectMapper.readValue(
                            registrationJson,
                            PendingRegistration.class
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read registration data",
                    e
            );
        }

        Role userRole =
                roleRepository.findByName("ROLE_USER")
                        .orElseThrow();

        User user = User.builder()
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .email(registration.getEmail())
                .phone(registration.getPhone())
                .password(
                        passwordEncoder.encode(
                                registration.getPassword()
                        )
                )
                .enabled(true)
                .rewardPoints(0)
                .role(userRole)
                .build();

        userRepository.save(user);

        redisTemplate.delete(
                "otp:" + request.email()
        );

        redisTemplate.delete(
                "register:" + request.email()
        );
    }




    @Transactional
    public void resendOtp(
            ResendOtpRequest request) {

        String key =
                "otp_resend:email:"
                        + request.getEmail();

        String countValue =
                redisTemplate.opsForValue()
                        .get(key);

        Integer count =
                countValue == null
                        ? 0
                        : Integer.parseInt(countValue);

        count = count == null
                ? 0
                : count;

        if(count >= 3) {

            throw new MaxResendLimitException();
        }

        String otp =
                otpGenerator.generateOtp();

        redisTemplate.opsForValue().set(
                "otp:email:"
                        + request.getEmail(),
                otp,
                Duration.ofMinutes(5)
        );

        redisTemplate.opsForValue().increment(
                key
        );

        redisTemplate.expire(
                key,
                Duration.ofMinutes(5)
        );

        emailService.sendOtpEmail(
                request.getEmail(),
                otp
        );
    }


}
