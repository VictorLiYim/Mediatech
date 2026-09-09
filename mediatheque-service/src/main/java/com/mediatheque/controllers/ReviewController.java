package com.mediatheque.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediatheque.dto.CreateReviewRequest;
import com.mediatheque.dto.ReviewResponse;
import com.mediatheque.services.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{id}/reviews")
    public ReviewResponse postReview(@PathVariable Long id, @Valid @RequestBody CreateReviewRequest request) {
        log.info("POST /books/{}/reviews userId={}", id, request.userId());
        return ReviewResponse.from(
                reviewService.postReview(id, request.userId(), request.rating(), request.comment())
        );
    }

    @GetMapping("/{id}/reviews")
    public List<ReviewResponse> getReviews(@PathVariable Long id) {
        log.info("GET /books/{}/reviews", id);
        return reviewService.getReviewsForBook(id).stream().map(ReviewResponse::from).toList();
    }
}
