        package com.cinebook.security.config;

import com.cinebook.dto.response.ApiErrorResponse;
import com.cinebook.security.JwtAuthenticationFilter;
import com.cinebook.security.RequestLoggingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final RequestLoggingFilter requestLoggingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        log.info("[SecurityConfig] Initializing security filter chain");

        http

                .csrf(csrf -> {
                    csrf.disable();
                    log.debug("[SecurityConfig] CSRF protection disabled");
                })

                .sessionManagement(session -> {
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS);
                    log.debug("[SecurityConfig] Session policy set to STATELESS");
                })

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/home/**",
                                "/api/movies/**",
                                "/api/theatres/**",
                                "/api/shows/**",
                                "/api/offers/**",
                                "/api/faqs/**",
                                "/api/help/**",
                                "/api/support/contact",
                                "/api/others/general"
                        ).permitAll()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/api/bookings/**",
                                "/api/profile/**",
                                "/api/preferences/**",
                                "/api/support/tickets/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exceptions ->

                        exceptions

                                .authenticationEntryPoint((request, response, authException) -> {

                                    log.error(
                                            "[SecurityConfig] Authentication failed for {}: {}",
                                            request.getRequestURI(),
                                            authException.getMessage()
                                    );

                                    ApiErrorResponse error =
                                            ApiErrorResponse.builder()
                                                    .timestamp(LocalDateTime.now())
                                                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                                                    .error("UNAUTHORIZED")
                                                    .message("Authentication is required to access this resource")
                                                    .path(request.getRequestURI())
                                                    .build();

                                    response.setContentType("application/json");
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                                    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

                                    response.getWriter()
                                            .write(mapper.writeValueAsString(error));
                                })

                                .accessDeniedHandler((request, response, accessDeniedException) -> {

                                    log.error(
                                            "[SecurityConfig] Access denied for {}: {}",
                                            request.getRequestURI(),
                                            accessDeniedException.getMessage()
                                    );

                                    ApiErrorResponse error =
                                            ApiErrorResponse.builder()
                                                    .timestamp(LocalDateTime.now())
                                                    .status(HttpServletResponse.SC_FORBIDDEN)
                                                    .error("ACCESS_DENIED")
                                                    .message("Admin privileges are required to access this resource")
                                                    .path(request.getRequestURI())
                                                    .build();

                                    response.setContentType("application/json");
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                                    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

                                    response.getWriter()
                                            .write(mapper.writeValueAsString(error));
                                })
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(
                        requestLoggingFilter,
                        JwtAuthenticationFilter.class
                );

        log.info("[SecurityConfig] Security filter chain configured successfully");

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
