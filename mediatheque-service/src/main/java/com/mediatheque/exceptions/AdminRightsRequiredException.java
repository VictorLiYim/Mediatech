package com.mediatheque.exceptions;

public class AdminRightsRequiredException extends RuntimeException {
    public AdminRightsRequiredException(Long userId) {
        super("User " + userId + " is not allowed to manage the stock (admin only)");
    }
}
