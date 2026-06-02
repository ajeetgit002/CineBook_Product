package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.BookingRequest;
import com.cinebook.dto.response.BookingResponse;
import com.cinebook.dto.response.TicketResponse;
import com.cinebook.entity.Booking;
import com.cinebook.entity.BookingSeat;
import com.cinebook.entity.Payment;
import com.cinebook.entity.Seat;
import com.cinebook.entity.Show;
import com.cinebook.entity.User;
import com.cinebook.enums.BookingStatus;
import com.cinebook.enums.PaymentStatus;
import com.cinebook.exceptions.ForbiddenException;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.exceptions.SeatAlreadyBookedException;
import com.cinebook.repository.BookingRepository;
import com.cinebook.repository.BookingSeatRepository;
import com.cinebook.repository.PaymentRepository;
import com.cinebook.repository.SeatRepository;
import com.cinebook.repository.ShowRepository;
import com.cinebook.security.CurrentUserService;
import com.cinebook.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PaymentRepository paymentRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public BookingResponse create(BookingRequest request) {
        User user = currentUserService.getCurrentUser();
        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<Seat> seats = request.seatIds().stream()
                .map(seatId -> seatRepository.findById(seatId)
                        .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId)))
                .toList();

        for (Seat seat : seats) {
            boolean booked = bookingSeatRepository.existsBySeatIdAndBookingShowIdAndBookingStatusIn(
                    seat.getId(),
                    show.getId(),
                    List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.UPCOMING)
            );
            if (booked) {
                throw new SeatAlreadyBookedException("Seat already booked: " + seat.getSeatNumber());
            }
        }

        BigDecimal unitPrice = show.getPrice() == null ? BigDecimal.ZERO : show.getPrice();
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(seats.size()));

        Booking booking = Booking.builder()
                .bookingCode("CB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .ticketCount(seats.size())
                .totalAmount(total)
                .status(BookingStatus.UPCOMING)
                .bookingTime(LocalDateTime.now())
                .user(user)
                .show(show)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        seats.forEach(seat -> bookingSeatRepository.save(BookingSeat.builder()
                .booking(savedBooking)
                .seat(seat)
                .price(unitPrice)
                .build()));

        paymentRepository.save(Payment.builder()
                .booking(savedBooking)
                .transactionId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(total)
                .paymentMethod(request.paymentMethod())
                .status(PaymentStatus.SUCCESS)
                .build());

        return toResponse(savedBooking);
    }

    @Override
    public List<BookingResponse> myBookings(BookingStatus status) {
        User user = currentUserService.getCurrentUser();
        List<Booking> bookings = status == null
                ? bookingRepository.findByUserId(user.getId())
                : bookingRepository.findByUserIdAndStatus(user.getId(), status);

        return bookings.stream().map(this::toResponse).toList();
    }

    @Override
    public BookingResponse getBooking(Long bookingId) {
        return toResponse(ownedBooking(bookingId));
    }

    @Override
    @Transactional
    public BookingResponse cancel(Long bookingId) {
        Booking booking = ownedBooking(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    public TicketResponse ticket(Long bookingId) {
        Booking booking = ownedBooking(bookingId);
        BookingResponse response = toResponse(booking);
        return new TicketResponse(response, "CINEBOOK:" + booking.getBookingCode());
    }

    @Override
    public List<BookingResponse> history() {
        return myBookings(null);
    }

    private Booking ownedBooking(Long bookingId) {
        User user = currentUserService.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean admin = user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().getName());
        if (!admin && !booking.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Booking does not belong to current user");
        }

        return booking;
    }

    private BookingResponse toResponse(Booking booking) {
        List<String> seatNumbers = bookingSeatRepository.findByBookingId(booking.getId()).stream()
                .map(bookingSeat -> bookingSeat.getSeat().getSeatNumber())
                .toList();

        return BookingResponse.from(booking, seatNumbers);
    }
}
