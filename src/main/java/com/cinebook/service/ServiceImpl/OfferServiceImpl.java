package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.OfferRequest;
import com.cinebook.dto.request.OfferValidationRequest;
import com.cinebook.dto.response.OfferResponse;
import com.cinebook.entity.Offer;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.OfferRepository;
import com.cinebook.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;

    @Override
    public List<OfferResponse> all() {
        return offerRepository.findByActiveTrue().stream()
                .map(OfferResponse::from)
                .toList();
    }

    @Override
    public List<OfferResponse> category(String category) {
        return offerRepository.findByCategoryIgnoreCaseAndActiveTrue(category).stream()
                .map(OfferResponse::from)
                .toList();
    }

    @Override
    public OfferResponse get(Long id) {
        return OfferResponse.from(findOffer(id));
    }

    @Override
    public Map<String, Object> validate(OfferValidationRequest request) {
        Offer offer = offerRepository.findByCouponCodeIgnoreCaseAndActiveTrue(request.couponCode())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        LocalDate today = LocalDate.now();
        boolean valid = (offer.getValidFrom() == null || !today.isBefore(offer.getValidFrom()))
                && (offer.getValidTo() == null || !today.isAfter(offer.getValidTo()));

        return Map.of(
                "valid", valid,
                "offer", OfferResponse.from(offer)
        );
    }

    @Override
    public OfferResponse create(OfferRequest request) {
        Offer offer = new Offer();
        apply(offer, request);
        return OfferResponse.from(offerRepository.save(offer));
    }

    @Override
    public OfferResponse update(Long id, OfferRequest request) {
        Offer offer = findOffer(id);
        apply(offer, request);
        return OfferResponse.from(offerRepository.save(offer));
    }

    @Override
    public void delete(Long id) {
        Offer offer = findOffer(id);
        offerRepository.delete(offer);
    }

    private Offer findOffer(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
    }

    private void apply(Offer offer, OfferRequest request) {
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setCategory(request.category());
        offer.setCouponCode(request.couponCode());
        offer.setDiscountValue(request.discountValue());
        offer.setValidFrom(request.validFrom());
        offer.setValidTo(request.validTo());
        offer.setActive(request.active() == null || request.active());
    }
}
