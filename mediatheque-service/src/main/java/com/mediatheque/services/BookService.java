package com.mediatheque.services;

import java.util.List;
import java.util.Set;

import com.mediatheque.models.Book;
import com.mediatheque.models.BookType;
import com.mediatheque.models.Genre;

public interface BookService {

    Book createBook(String title, Set<Long> authorIds, String isbn, BookType type,
                    String description, Set<Genre> genres, int totalCopies);

    List<Book> getAllBooks();

    Book getById(Long id);

    void updateAverageRating(Long bookId, double newAverage);

    /** Décrémente availableCopies ; lève BookNotAvailableException si aucun exemplaire libre. */
    void borrowCopy(Long bookId);

    /** Incrémente availableCopies (plafonné à totalCopies). */
    void returnCopy(Long bookId);
}