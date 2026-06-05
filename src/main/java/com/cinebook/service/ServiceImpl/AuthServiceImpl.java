package com.cinebook.service.ServiceImpl;


import com.cinebook.dto.request.ForgotPasswordRequest;
import com.cinebook.dto.request.LoginRequest;
import com.cinebook.dto.request.RegisterRequest;
import com.cinebook.dto.request.ResetPasswordRequest;
import com.cinebook.dto.request.TokenRefreshRequest;
import com.cinebook.dto.request.VerifyOtpRequest;
import com.cinebook.dto.response.AuthResponse;
import com.cinebook.dto.response.RegisterResponse;
import com.cinebook.entity.Role;
import com.cinebook.entity.User;
import com.cinebook.exceptions.DuplicateResourceException;
import com.cinebook.exceptions.ForbiddenException;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.exceptions.UnauthorizedException;
import com.cinebook.repository.RoleRepository;
import com.cinebook.repository.UserRepository;
import com.cinebook.security.JwtService;
import com.cinebook.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {



    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;





    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered");
        }

        if (request.getPhone() != null
                && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException(
                    "Phone already registered");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name("ROLE_USER").build()));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .enabled(true)
                .rewardPoints(0)
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getName())
                .build();
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
    public String forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
    public String verifyOtp(VerifyOtpRequest request) {
        userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return "OTP verified successfully";
    }
}
