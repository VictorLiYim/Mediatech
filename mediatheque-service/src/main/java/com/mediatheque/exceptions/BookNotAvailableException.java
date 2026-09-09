package com.mediatheque.exceptions;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(Long bookId) {
        super("No copy available for book " + bookId);
    }
}
