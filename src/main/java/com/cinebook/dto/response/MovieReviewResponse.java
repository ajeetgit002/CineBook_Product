package com.cinebook.dto.response;

import com.cinebook.entity.MovieReview;

import java.time.LocalDateTime;

public record MovieReviewResponse(
        Long id,
        Integer rating,
        String comment,
        String reviewerName,
        LocalDateTime createdAt
) {
    public static MovieReviewResponse from(MovieReview review) {
        String reviewerName = review.getUser() == null
                ? "CineBook user"
                : review.getUser().getFirstName() + " " + review.getUser().getLastName();
        return new MovieReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                reviewerName.trim(),
                review.getCreatedAt()
        );
    }
}
