package com.mediatheque.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediatheque.models.Review;
import com.mediatheque.repositories.ReviewRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final BookService bookService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, BookService bookService) {
        this.reviewRepository = reviewRepository;
        this.bookService = bookService;
    }

    @Override
    @Transactional
    public Review postReview(Long bookId, Long userId, int rating, String comment) {
        log.debug("New review bookId={} userId={} rating={}", bookId, userId, rating);
        bookService.getById(bookId); // vérifie l'existence, lève BookNotFoundException sinon

        Review review = new Review(bookId, userId, rating, comment);
        Review saved = reviewRepository.save(review);

        recomputeAverageRating(bookId);
        log.info("Review id={} posted on book {}", saved.getId(), bookId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    private void recomputeAverageRating(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        bookService.updateAverageRating(bookId, average);
    }
}
