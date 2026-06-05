
package com.cinebook.dto.response;

public record CreateOrderResponse(

        String orderId,
        String currency,
        Integer amount,
        String key

) {
}

