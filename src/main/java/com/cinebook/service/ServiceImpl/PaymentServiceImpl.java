
        package com.cinebook.service.ServiceImpl;

import com.cinebook.config.RazorpayConfig;
import com.cinebook.dto.request.CreateOrderRequest;
import com.cinebook.dto.request.VerifyPaymentRequest;
import com.cinebook.dto.response.CreateOrderResponse;
import com.cinebook.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Override
    public CreateOrderResponse createOrder(
            CreateOrderRequest request
    ) throws Exception {

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                request.amount().intValue() * 100
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                "booking_" + request.bookingId()
        );


        Order order = razorpayClient.orders.create(orderRequest);



        return new CreateOrderResponse(
                order.get("id"),
                order.get("currency"),
                order.get("amount"),
                razorpayConfig.getKeyId()
        );
    }


    @Override
    public boolean verifyPayment(
            VerifyPaymentRequest request
    ) throws Exception {

        String payload =
                request.razorpayOrderId()
                        + "|"
                        + request.razorpayPaymentId();

        return Utils.verifySignature(
                payload,
                request.razorpaySignature(),
                razorpayConfig.getKeySecret()
        );
    }


}
