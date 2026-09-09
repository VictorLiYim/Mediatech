package com.mediatheque.services;

import java.util.List;

import com.mediatheque.models.Review;

public interface ReviewService {

    Review postReview(Long bookId, Long userId, int rating, String comment);

    List<Review> getReviewsForBook(Long bookId);
}
