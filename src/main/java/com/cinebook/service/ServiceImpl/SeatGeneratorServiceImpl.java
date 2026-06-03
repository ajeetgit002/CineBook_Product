
        package com.cinebook.service.ServiceImpl;

import com.cinebook.entity.Screen;
import com.cinebook.entity.Seat;
import com.cinebook.enums.SeatType;
import com.cinebook.repository.SeatRepository;
import com.cinebook.service.SeatGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatGeneratorServiceImpl
        implements SeatGeneratorService {

    private final SeatRepository seatRepository;

    @Override
    public void generateSeats(Screen screen) {

        List<Seat> seats = new ArrayList<>();

        char row = 'A';

        int seatsPerRow = 10;

        int totalSeats = screen.getCapacity();

        for (int i = 1; i <= totalSeats; i++) {

            String seatNumber =
                    row + String.valueOf(
                            ((i - 1) % seatsPerRow) + 1
                    );

            if (i % seatsPerRow == 0) {
                row++;
            }

            seats.add(
                    Seat.builder()
                            .screen(screen)
                            .seatNumber(seatNumber)
                            .seatType(SeatType.REGULAR)
                            .basePrice(BigDecimal.valueOf(250))
                            .build()
            );
        }

        seatRepository.saveAll(seats);
    }
}
