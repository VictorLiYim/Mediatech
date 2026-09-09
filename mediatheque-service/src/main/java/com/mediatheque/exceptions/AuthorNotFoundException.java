package com.mediatheque.exceptions;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(Long id) {
        super("No author found with id "+ id);

    }
}
