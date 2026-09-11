package com.mediatheque.models;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "book_id", nullable = false)
    private Long bookId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(nullable = false)
    private int rating;
    @Column(length = 1000)
    private String comment;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    public Review(Long bookId, long userId, int rating, String comment){
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Instant.now();
    }
}
