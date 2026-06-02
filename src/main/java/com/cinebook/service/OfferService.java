package com.cinebook.service;

import com.cinebook.dto.request.OfferRequest;
import com.cinebook.dto.request.OfferValidationRequest;
import com.cinebook.dto.response.OfferResponse;

import java.util.List;
import java.util.Map;

public interface OfferService {
    List<OfferResponse> all();

    List<OfferResponse> category(String category);

    OfferResponse get(Long id);

    Map<String, Object> validate(OfferValidationRequest request);

    OfferResponse create(OfferRequest request);

    OfferResponse update(Long id, OfferRequest request);

    void delete(Long id);
}
