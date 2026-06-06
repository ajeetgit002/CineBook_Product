package com.cinebook.service;

import com.cinebook.entity.Booking;

public interface EmailService {

    void sendOtpEmail(
            String email,
            String otp
    );

    void sendTicketEmail(
            Booking booking
    );
}