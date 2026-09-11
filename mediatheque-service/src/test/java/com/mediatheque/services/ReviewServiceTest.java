package com.mediatheque.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediatheque.models.Review;
import com.mediatheque.repositories.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private BookService bookService;

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Test
	void postReview_savesReviewAndRecomputesAverageRating() {
		Review saved = new Review(1L, 42L, 5, "Un chef-d'œuvre");
		when(reviewRepository.save(any(Review.class))).thenReturn(saved);
		when(reviewRepository.findByBookId(1L)).thenReturn(List.of(saved));

		Review result = reviewService.postReview(1L, 42L, 5, "Un chef-d'œuvre");

		verify(bookService).getById(1L);
		verify(bookService).updateAverageRating(1L, 5.0);
		assertThat(result).isSameAs(saved);
	}

	@Test
	void postReview_averagesMultipleRatings() {
		Review existing = new Review(1L, 7L, 3, "Bien");
		Review saved = new Review(1L, 42L, 5, "Un chef-d'œuvre");
		when(reviewRepository.save(any(Review.class))).thenReturn(saved);
		when(reviewRepository.findByBookId(1L)).thenReturn(List.of(existing, saved));

		reviewService.postReview(1L, 42L, 5, "Un chef-d'œuvre");

		verify(bookService).updateAverageRating(1L, 4.0);
	}

	@Test
	void getReviewsForBook_returnsRepositoryResult() {
		Review review = new Review(1L, 42L, 5, "Un chef-d'œuvre");
		when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));

		List<Review> result = reviewService.getReviewsForBook(1L);

		assertThat(result).containsExactly(review);
	}
}
