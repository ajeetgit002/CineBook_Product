
        package com.cinebook.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .maximumSize(100_000)
            .build();

    private final JwtService jwtService;

    public RateLimitFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String endpoint = request.getRequestURI();

        // ✅ 1. Skip system/health endpoints
        if (isWhitelisted(endpoint)) {
            chain.doFilter(request, response);
            return;
        }

        String identity = resolveIdentity(request);
        String key = endpoint + ":" + identity;

        Bucket bucket = cache.get(key, k -> createBucket(endpoint));

        if (bucket.tryConsume(1)) {

            response.setHeader(
                    "X-RateLimit-Remaining",
                    String.valueOf(bucket.getAvailableTokens())
            );

            chain.doFilter(request, response);

        } else {

            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "60");

            response.getWriter().write("""
                {
                  "status": 429,
                  "error": "TOO_MANY_REQUESTS",
                  "message": "Too many requests. Please try again later.",
                  "retryAfterSeconds": 60
                }
            """);
        }
    }

    // =========================
    // BUCKET CONFIG
    // =========================
    private Bucket createBucket(String endpoint) {

        Bandwidth limit;

        if (endpoint.contains("/auth/login")) {

            // 🔥 stricter login protection
            limit = Bandwidth.classic(
                    3,
                    Refill.greedy(3, Duration.ofMinutes(1))
            );

        } else if (endpoint.contains("/auth/register")) {

            limit = Bandwidth.classic(
                    10,
                    Refill.greedy(10, Duration.ofMinutes(1))
            );

        } else {

            limit = Bandwidth.classic(
                    100,
                    Refill.greedy(100, Duration.ofMinutes(1))
            );
        }

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // =========================
    // IDENTITY RESOLUTION
    // =========================
    private String resolveIdentity(HttpServletRequest request) {

        // 1. Try JWT (logged-in user)
        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                String token = auth.substring(7);
                String email = jwtService.extractUsername(token);

                if (email != null) {
                    return "USER:" + email;
                }
            } catch (Exception ignored) {}
        }

        // 2. Safe IP extraction (proxy-aware)
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].trim();

            // basic safety check
            if (ip.length() > 45) {
                ip = request.getRemoteAddr();
            }

        } else {
            ip = request.getRemoteAddr();
        }

        return "IP:" + ip;
    }

    // =========================
    // WHITELIST
    // =========================
    private boolean isWhitelisted(String endpoint) {

        return endpoint.startsWith("/actuator")
                || endpoint.startsWith("/health")
                || endpoint.startsWith("/swagger")
                || endpoint.startsWith("/v3/api-docs");
    }
}

