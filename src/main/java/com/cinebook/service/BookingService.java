package com.cinebook.service;

import com.cinebook.dto.request.BookingRequest;
import com.cinebook.dto.response.BookingResponse;
import com.cinebook.dto.response.TicketResponse;
import com.cinebook.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponse create(BookingRequest request);

    List<BookingResponse> myBookings(BookingStatus status);

    BookingResponse getBooking(Long bookingId);

    BookingResponse cancel(Long bookingId);

    TicketResponse ticket(Long bookingId);

    List<BookingResponse> history();

    void confirmBooking(Long bookingId);


}
