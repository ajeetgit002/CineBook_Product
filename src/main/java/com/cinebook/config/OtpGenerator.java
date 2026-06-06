package com.cinebook.config;


import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class OtpGenerator {

    public String generateOtp() {

        return String.valueOf(
                ThreadLocalRandom.current()
                        .nextInt(100000, 999999)
        );
    }
}