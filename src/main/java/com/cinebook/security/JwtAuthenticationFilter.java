package com.cinebook.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        log.debug("[JWT Filter] Processing {} request to: {}", method, requestPath);

        String header =
                request.getHeader("Authorization");

        if (header == null ||
                !header.startsWith("Bearer ")) {

            log.debug("[JWT Filter] No Bearer token found for path: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token =
                    header.substring(7);

            log.debug("[JWT Filter] Bearer token found, extracting username...");

            String email =
                    jwtService.extractUsername(token);

            log.debug("[JWT Filter] Extracted email: {}", email);

            if (email != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                log.debug("[JWT Filter] Loading user details for: {}", email);

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                log.debug("[JWT Filter] User authorities: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);

                log.info("[JWT Filter] Successfully authenticated user: {} for path: {}", email, requestPath);
            } else {
                log.warn("[JWT Filter] Could not authenticate - email null or already authenticated");
            }
        } catch (Exception e) {
            log.error("[JWT Filter] Authentication error for path: {} - {}", requestPath, e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}