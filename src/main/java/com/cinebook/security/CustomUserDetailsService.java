package com.cinebook.security;

import com.cinebook.entity.User;
import com.cinebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        log.debug("[UserDetailsService] Loading user details for email: {}", email);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[UserDetailsService] User not found with email: {}", email);
                    return new UsernameNotFoundException(
                            "User not found: " + email);
                });

        log.debug("[UserDetailsService] User found: {}, Role: {}, Enabled: {}",
                email, user.getRole().getName(), user.getEnabled());

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(
                        user.getRole()
                                .getName()
                                .replace("ROLE_", "")
                )
                .disabled(Boolean.FALSE.equals(user.getEnabled()))
                .build();

        log.debug("[UserDetailsService] UserDetails created with authorities: {}", userDetails.getAuthorities());

        return userDetails;
    }
}
