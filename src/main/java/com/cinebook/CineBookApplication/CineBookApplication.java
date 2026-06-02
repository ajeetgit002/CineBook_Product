package com.cinebook.CineBookApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.cinebook")
@EntityScan(basePackages = "com.cinebook.entity")
@EnableJpaRepositories(basePackages = "com.cinebook.repository")
public class CineBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(CineBookApplication.class, args);
    }
}
