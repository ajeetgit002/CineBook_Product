
        package com.cinebook.service.ServiceImpl;

import com.cinebook.entity.RefreshToken;
import com.cinebook.entity.User;
import com.cinebook.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    public RefreshToken createRefreshToken(User user) {
        Optional<RefreshToken> existingToken =
                refreshTokenRepository.findAllByUser(user)
                        .stream()
                        .findFirst();

        if (existingToken.isPresent()) {

            RefreshToken token =
                    existingToken.get();

            token.setToken(
                    UUID.randomUUID().toString()
            );

            token.setExpiryDate(
                    LocalDateTime.now().plusDays(7)
            );

            token.setRevoked(false);

            return refreshTokenRepository.save(token);
        }

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusDays(7)
                        )
                        .revoked(false)
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }


    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }
}

