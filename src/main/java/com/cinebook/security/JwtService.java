package com.cinebook.security;

import com.cinebook.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:cinebook-development-secret-key-minimum-256-bit-length}")
    private String SECRET;

    private static final long JWT_EXPIRATION =
            1000 * 60 * 60;

    public String generateToken(User user) {

        try {

            Map<String, Object> claims = new HashMap<>();

            claims.put("role", user.getRole().getName());
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());

            String token = Jwts.builder()

                    .setClaims(claims)

                    .setSubject(user.getEmail())

                    .setIssuedAt(new Date())

                    .setExpiration(
                            new Date(
                                    System.currentTimeMillis()
                                            + JWT_EXPIRATION
                            )
                    )

                    .signWith(getKey(), SignatureAlgorithm.HS256)

                    .compact();

            log.info(
                    "[JwtService] Token generated successfully for email: {}",
                    user.getEmail()
            );

            return token;

        } catch (Exception e) {

            log.error(
                    "[JwtService] Error generating token for email: {} - {}",
                    user.getEmail(),
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }

    public String extractUsername(String token) {

        try {

            String username = Jwts.parser()

                    .setSigningKey(getKey())

                    .parseClaimsJws(token)

                    .getBody()

                    .getSubject();

            log.debug(
                    "[JwtService] Token extracted username: {}",
                    username
            );

            return username;

        } catch (io.jsonwebtoken.ExpiredJwtException e) {

            log.warn("[JwtService] Token expired");

            throw new RuntimeException("Token has expired", e);

        } catch (io.jsonwebtoken.SignatureException e) {

            log.warn("[JwtService] Invalid token signature");

            throw new RuntimeException("Invalid token signature", e);

        } catch (Exception e) {

            log.error(
                    "[JwtService] Error extracting username from token - {}",
                    e.getMessage(),
                    e
            );

            throw new RuntimeException("Error processing token", e);
        }
    }

    private SecretKey getKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }
}