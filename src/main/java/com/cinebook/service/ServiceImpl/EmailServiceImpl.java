package com.cinebook.service.ServiceImpl;

import com.cinebook.entity.Booking;
import com.cinebook.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;

import org.springframework.mail.javamail.MimeMessageHelper;


import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(
            String email,
            String otp
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(email);
            helper.setSubject(
                    "CineBook Account Verification"
            );




            String html = """
<html>
<body style="
    margin:0;
    padding:0;
    background-color:#f4f4f4;
    font-family:Arial,sans-serif;">

<div style="
    max-width:600px;
    margin:30px auto;
    background:#ffffff;
    border-radius:12px;
    overflow:hidden;
    box-shadow:0 2px 10px rgba(0,0,0,0.1);">

    <!-- Header -->
    <div style="
        background:#111111;
        text-align:center;
        padding:25px;">

        <img src="https://cdn.jsdelivr.net/gh/ajeetgit002/CineBook_Product@main/src/main/resources/assets/logo.png"
             width="280"
             alt="CineBook"/>
    </div>

    <!-- Content -->
    <div style="padding:30px;">

        <h2 style="
            color:#E50914;
            text-align:center;
            margin-bottom:20px;">
            Email Verification
        </h2>

        <p style="
            font-size:16px;
            color:#333;">
            Welcome to <b>CineBook</b> 🎬
        </p>

        <p style="
            font-size:15px;
            color:#555;">
            Thank you for registering with CineBook.
            Use the OTP below to verify your account and continue booking movies.
        </p>

        <div style="
            text-align:center;
            margin:35px 0;">

            <div style="
                display:inline-block;
                background:#fff5f5;
                border:2px dashed #E50914;
                border-radius:10px;
                padding:15px 30px;
                font-size:36px;
                font-weight:bold;
                letter-spacing:8px;
                color:#E50914;">
                %s
            </div>

        </div>

        <p style="
            text-align:center;
            color:#555;">
            OTP is valid for
            <b>5 minutes</b>.
        </p>

        <hr style="
            border:none;
            border-top:1px solid #eeeeee;
            margin:25px 0;">

        <p style="
            font-size:13px;
            color:#777;
            text-align:center;">
            If you didn't request this OTP,
            please ignore this email.
        </p>

    </div>

    <!-- Footer -->
    <div style="
        background:#111111;
        color:#ffffff;
        text-align:center;
        padding:15px;
        font-size:12px;">

        © 2026 CineBook | Book Movies • Watch OTT

    </div>

</div>

</body>
</html>
""".formatted(otp);

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send email",
                    e
            );
        }
    }
    @Override
    public void sendTicketEmail(
            Booking booking
    ) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setTo(
                    booking.getUser().getEmail()
            );

            helper.setSubject(
                    "CineBook Ticket Confirmation"
            );

            helper.setText(
                    """
                    Booking Confirmed
    
                    Booking Code: %s
                    Movie: %s
    
                    Enjoy your show!
                    """
                            .formatted(
                                    booking.getBookingCode(),
                                    booking.getShow()
                                            .getMovie()
                                            .getTitle()
                            )
            );

            FileSystemResource qr =
                    new FileSystemResource(
                            booking.getQrCodePath()
                    );

            helper.addAttachment(
                    booking.getBookingCode()
                            + "-ticket.png",
                    new FileSystemResource(
                            booking.getTicketPath()
                    )
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send ticket email",
                    e
            );
        }
    }
}