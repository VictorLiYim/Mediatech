package com.mediatheque.exceptions;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String isbn) {
        super("A book already exists with isbn '" + isbn + "'");
    }
}
