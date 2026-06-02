package com.cinebook.repository;

import com.cinebook.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByActiveTrue();

    List<Offer> findByCategoryIgnoreCaseAndActiveTrue(String category);

    Optional<Offer> findByCouponCodeIgnoreCaseAndActiveTrue(String couponCode);
}
