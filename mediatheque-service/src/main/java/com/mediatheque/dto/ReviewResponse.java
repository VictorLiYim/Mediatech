package com.mediatheque.dto;

import com.mediatheque.models.Review;

import java.time.Instant;

public record ReviewResponse(Long id, Long bookId, Long userId, int rating, String comment, Instant created_at) {
    public static ReviewResponse from(Review review){
        return new ReviewResponse(
                review.getId(), review.getBookId(), review.getUserId(), review.getRating(), review.getComment(), review.getCreatedAt()
        );
    }
}
