package com.cinebook.service.ServiceImpl;

import com.cinebook.entity.Booking;
import com.cinebook.entity.BookingSeat;
import com.cinebook.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
@RequiredArgsConstructor
public class TicketGeneratorService {
    private final BookingSeatRepository bookingSeatRepository;
    public String generateTicket(
            Booking booking
    ) throws Exception {

        Files.createDirectories(
                Path.of("tickets")
        );

        BufferedImage template =
                ImageIO.read(
                        new ClassPathResource(
                                "assets/ticket-template.png"
                        ).getInputStream()
                );

        Graphics2D g =
                template.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (booking.getShow()
                .getMovie()
                .getPosterUrl() != null) {

            BufferedImage poster =
                    ImageIO.read(
                            new URL(
                                    booking.getShow()
                                            .getMovie()
                                            .getPosterUrl()
                            )
                    );

            g.drawImage(
                    poster,
                    130,
                    370,
                    270,
                    330,
                    null
            );
        }

// Movie Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 44));

        g.drawString(
                booking.getShow()
                        .getMovie()
                        .getTitle(),
                450,
                430
        );

// Date
        g.setFont(new Font("Arial", Font.PLAIN, 28));

        g.drawString(
                booking.getShow()
                        .getShowDate()
                        .toString(),
                500,
                520
        );

// Time
        g.drawString(
                booking.getShow()
                        .getStartTime()
                        .toString(),
                500,
                590
        );

// Venue
        g.drawString(
                booking.getShow()
                        .getScreen()
                        .getTheatre()
                        .getName(),
                500,
                670
        );

// Screen
        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30
                )
        );

        g.drawString(
                booking.getShow()
                        .getScreen()
                        .getName(),
                250,
                815
        );



        // ==========================
        // Seats
        // ==========================

        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBookingId(
                        booking.getId()
                );

        String seats =
                bookingSeats.stream()
                        .map(bs ->
                                bs.getSeat()
                                        .getSeatNumber())
                        .collect(
                                Collectors.joining(", ")
                        );

        // Seats
        g.drawString(
                seats,
                690,
                815
        );

        g.drawString(
                "01",
                500,
                815
        );
        // ==========================
        // Booking Code
        // ==========================

        g.setColor(
                new Color(
                        255,
                        0,
                        0
                )
        );

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        g.drawString(
                "BOOKING ID",
                420,
                1160
        );
        g.setColor(
                Color.WHITE
        );
        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );
        g.drawString(
                booking.getBookingCode(),
                400,
                1185
        );

        // ==========================
        // QR Image
        // ==========================

        BufferedImage qr =
                ImageIO.read(
                        new File(
                                booking.getQrCodePath()
                        )
                );

        g.drawImage(
                qr,
                360,
                860,
                260,
                260,
                null
        );

        g.dispose();

        String output =
                "tickets/"
                        + booking.getBookingCode()
                        + "-ticket.png";

        ImageIO.write(
                template,
                "png",
                new File(output)
        );

        return output;
    }
}