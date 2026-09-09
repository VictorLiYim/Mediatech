package com.mediatheque.exceptions;

public class BookNotCurrentlyBorrowedException extends RuntimeException {
    public BookNotCurrentlyBorrowedException(Long bookId, Long userId) {
        super("No active loan for book " + bookId + " and user " + userId);
    }
}

