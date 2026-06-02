package com.cinebook.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String path = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        log.info("[REQUEST] {} {} from {} Headers: ", method, path + queryString, request.getRemoteAddr());

        // Log Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                log.debug("[REQUEST] Authorization: Bearer [TOKEN_PRESENT]");
            } else {
                log.debug("[REQUEST] Authorization: {}", authHeader);
            }
        } else {
            log.debug("[REQUEST] Authorization: NONE");
        }

        // Log all request headers for debugging
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (!headerName.equalsIgnoreCase("Authorization")) {
                log.debug("[REQUEST] Header - {}: {}", headerName, headerValue);
            }
        }

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (status >= 400) {
                log.warn("[RESPONSE] {} {} - Status: {} ({}ms)", method, path + queryString, status, duration);
            } else {
                log.info("[RESPONSE] {} {} - Status: {} ({}ms)", method, path + queryString, status, duration);
            }
        }
    }
}

