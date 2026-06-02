package com.cinebook.controller;

import com.cinebook.dto.request.OfferValidationRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.OfferResponse;
import com.cinebook.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public ApiResponse<List<OfferResponse>> offers() {
        return ApiResponse.success("Offers fetched successfully", offerService.all());
    }

    @GetMapping("/deal-of-day")
    public ApiResponse<List<OfferResponse>> dealOfDay() {
        return ApiResponse.success("Deal of day fetched successfully", offerService.category("DEAL_OF_DAY"));
    }

    @GetMapping("/bank")
    public ApiResponse<List<OfferResponse>> bank() {
        return ApiResponse.success("Bank offers fetched successfully", offerService.category("BANK"));
    }

    @GetMapping("/cashback")
    public ApiResponse<List<OfferResponse>> cashback() {
        return ApiResponse.success("Cashback offers fetched successfully", offerService.category("CASHBACK"));
    }

    @GetMapping("/combo")
    public ApiResponse<List<OfferResponse>> combo() {
        return ApiResponse.success("Combo offers fetched successfully", offerService.category("COMBO"));
    }

    @GetMapping("/seasonal")
    public ApiResponse<List<OfferResponse>> seasonal() {
        return ApiResponse.success("Seasonal offers fetched successfully", offerService.category("SEASONAL"));
    }

    @GetMapping("/{offerId}")
    public ApiResponse<OfferResponse> offer(@PathVariable Long offerId) {
        return ApiResponse.success("Offer fetched successfully", offerService.get(offerId));
    }

    @PostMapping("/validate")
    public ApiResponse<Map<String, Object>> validate(@Valid @RequestBody OfferValidationRequest request) {
        return ApiResponse.success("Coupon validated successfully", offerService.validate(request));
    }
}
