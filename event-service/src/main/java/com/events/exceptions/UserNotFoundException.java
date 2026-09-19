package com.events.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("No user found with id " + userId);
    }
}
