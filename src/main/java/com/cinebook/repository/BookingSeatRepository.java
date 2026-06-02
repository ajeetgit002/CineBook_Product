package com.cinebook.repository;

import com.cinebook.entity.BookingSeat;
import com.cinebook.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BookingSeatRepository
        extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(Long bookingId);

    List<BookingSeat> findByBookingShowIdAndBookingStatusIn(
            Long showId,
            Collection<BookingStatus> statuses);

    boolean existsBySeatIdAndBookingShowIdAndBookingStatusIn(
            Long seatId,
            Long showId,
            Collection<BookingStatus> statuses);
}
