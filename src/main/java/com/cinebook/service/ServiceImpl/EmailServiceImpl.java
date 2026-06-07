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
<body>
<h2>CineBook Verification</h2>

<p>Your OTP is:</p>

<h1 style="color:red;">%s</h1>

<p>OTP is valid for 5 minutes.</p>
</body>
</html>
"""
                    .formatted(otp);

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