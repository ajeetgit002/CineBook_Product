package com.cinebook.config;

import com.cinebook.entity.Role;
import com.cinebook.entity.User;
import com.cinebook.repository.RoleRepository;
import com.cinebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {

            createRoleIfMissing("ROLE_USER");
            createRoleIfMissing("ROLE_ADMIN");

            createAdminIfMissing();
        };
    }

    private void createRoleIfMissing(String name) {

        if (!roleRepository.findByName(name).isPresent()) {

            roleRepository.save(
                    Role.builder()
                            .name(name)
                            .build()
            );
        }
    }

    private void createAdminIfMissing() {

        String adminEmail = "admin@cinebook.com";

        if (!userRepository.existsByEmail(adminEmail)) {

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@cinebook.com")
                    .phone("9999999999")
                    .password(passwordEncoder.encode("admin123"))
                    .rewardPoints(0)
                    .enabled(true)
                    .role(adminRole)
                    .build();

            userRepository.save(admin);

            System.out.println("Admin user created successfully");
        }
    }
}