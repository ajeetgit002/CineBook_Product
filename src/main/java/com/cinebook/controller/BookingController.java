package com.cinebook.controller;

import com.cinebook.dto.request.BookingRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.BookingResponse;
import com.cinebook.dto.response.TicketResponse;

import com.cinebook.enums.BookingStatus;
import com.cinebook.service.BookingService;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ApiResponse<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ApiResponse.success("Booking created successfully", bookingService.create(request));
    }

    @GetMapping
    public ApiResponse<List<BookingResponse>> filtered(@RequestParam(required = false) BookingStatus status) {
        return ApiResponse.success("Bookings fetched successfully", bookingService.myBookings(status));
    }

    @GetMapping("/my")
    public ApiResponse<List<BookingResponse>> myBookings() {
        return ApiResponse.success("My bookings fetched successfully", bookingService.myBookings(null));
    }

    @GetMapping("/history")
    public ApiResponse<List<BookingResponse>> history() {
        return ApiResponse.success("Booking history fetched successfully", bookingService.history());
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<BookingResponse> booking(@PathVariable Long bookingId) {
        return ApiResponse.success("Booking fetched successfully", bookingService.getBooking(bookingId));
    }

    @PutMapping("/{bookingId}/cancel")
    public ApiResponse<BookingResponse> cancel(@PathVariable Long bookingId) {
        return ApiResponse.success("Booking cancelled successfully", bookingService.cancel(bookingId));
    }

    @GetMapping("/{bookingId}/ticket")
    public ApiResponse<TicketResponse> ticket(@PathVariable Long bookingId) {
        return ApiResponse.success("Ticket fetched successfully", bookingService.ticket(bookingId));
    }

    @GetMapping("/{bookingId}/download-ticket")
    public ResponseEntity<Resource> downloadTicket(
            @PathVariable Long bookingId
    ) {

        Resource resource =
                bookingService.downloadTicket(
                        bookingId
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ticket.png"
                )
                .contentType(
                        MediaType.IMAGE_PNG
                )
                .body(resource);
    }

}
