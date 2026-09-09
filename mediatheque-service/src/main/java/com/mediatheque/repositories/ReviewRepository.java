package com.mediatheque.repositories;

import com.mediatheque.models.Review;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
}
