
        package com.cinebook.controller;

import com.cinebook.dto.request.CreateOrderRequest;
import com.cinebook.dto.request.VerifyPaymentRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.CreateOrderResponse;
import com.cinebook.service.BookingService;
import com.cinebook.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private final BookingService bookingService;


    @PostMapping("/create-order")
    public ApiResponse<CreateOrderResponse>
    createOrder(
            @Valid
            @RequestBody
            CreateOrderRequest request
    ) throws Exception {

        return ApiResponse.success(
                "Order created successfully",
                paymentService.createOrder(request)
        );
    }


    @PostMapping("/verify")
    public ApiResponse<Boolean>
    verifyPayment(
            @Valid
            @RequestBody
            VerifyPaymentRequest request
    ) throws Exception {

        boolean verified =
                paymentService.verifyPayment(request);

        if (verified) {

            bookingService.confirmBooking(
                    request.bookingId()
            );
        }



        return ApiResponse.success(
                "Payment verification completed",
                verified
        );
    }


}

