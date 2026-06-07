package com.cinebook.service.ServiceImpl;

import com.cinebook.entity.Booking;
import com.cinebook.service.EmailService;

import lombok.RequiredArgsConstructor;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final RestTemplate restTemplate;
    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;
    @Override
    public void sendOtpEmail(
            String email,
            String otp
    ) {

        try {

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

    <div style="
        background:#111111;
        text-align:center;
        padding:25px;">

        <img src="https://cdn.jsdelivr.net/gh/ajeetgit002/CineBook_Product@main/src/main/resources/assets/logo.png"
             width="280"
             alt="CineBook"/>
    </div>

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

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON
            );

            headers.set(
                    "api-key",
                    apiKey
            );

            String payload = """
{
  "sender": {
    "name": "%s",
    "email": "%s"
  },
  "to": [
    {
      "email": "%s"
    }
  ],
  "subject": "CineBook Account Verification",
  "htmlContent": %s
}
"""
                    .formatted(
                            senderName,
                            senderEmail,
                            email,
                            "\"" + html
                                    .replace("\"", "\\\"")
                                    .replace("\n", "")
                                    .replace("\r", "")
                                    + "\""
                    );

            HttpEntity<String> request =
                    new HttpEntity<>(
                            payload,
                            headers
                    );

            restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send OTP email",
                    e
            );
        }
    }
    @Override
    public void sendTicketEmail(Booking booking) {

        try {

            byte[] ticketBytes = Files.readAllBytes(
                    Path.of(booking.getTicketPath())
            );

            String base64Ticket =
                    Base64.getEncoder()
                            .encodeToString(ticketBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            String html = """
                <h2>Booking Confirmed 🎬</h2>
                <p><b>Booking Code:</b> %s</p>
                <p><b>Movie:</b> %s</p>
                <p>Enjoy your show!</p>
                """
                    .formatted(
                            booking.getBookingCode(),
                            booking.getShow()
                                    .getMovie()
                                    .getTitle()
                    );

            String payload = """
{
  "sender": {
    "name": "%s",
    "email": "%s"
  },
  "to": [
    {
      "email": "%s"
    }
  ],
  "subject": "CineBook Ticket Confirmation",
  "htmlContent": %s,
  "attachment": [
    {
      "name": "%s-ticket.png",
      "content": "%s"
    }
  ]
}
"""
                    .formatted(
                            senderName,
                            senderEmail,
                            booking.getUser().getEmail(),
                            "\"" + html.replace("\"", "\\\"")
                                    .replace("\n", "")
                                    .replace("\r", "") + "\"",
                            booking.getBookingCode(),
                            base64Ticket
                    );

            HttpEntity<String> request =
                    new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    request,
                    String.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send ticket email",
                    e
            );
        }
    }
}