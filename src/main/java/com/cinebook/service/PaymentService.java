
        package com.cinebook.service;

import com.cinebook.dto.request.CreateOrderRequest;
import com.cinebook.dto.request.VerifyPaymentRequest;
import com.cinebook.dto.response.CreateOrderResponse;

public interface PaymentService {

    CreateOrderResponse createOrder(
            CreateOrderRequest request
    ) throws Exception;
    boolean verifyPayment( VerifyPaymentRequest request ) throws Exception;
}

