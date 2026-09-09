package com.mediatheque.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.mediatheque.models.Book;
import com.mediatheque.models.BookType;
import com.mediatheque.models.Genre;

public record BookResponse(
        Long id,
        String title,
        Set<AuthorResponse> authors,
        String isbn,
        BookType type,
        String description,
        Set<Genre> genres,
        int totalCopies,
        int availableCopies,
        Double averageRating
) {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthors().stream().map(AuthorResponse::from).collect(Collectors.toSet()),
                book.getIsbn(),
                book.getType(),
                book.getDescription(),
                book.getGenres(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getAverageRating()
        );
    }
}
