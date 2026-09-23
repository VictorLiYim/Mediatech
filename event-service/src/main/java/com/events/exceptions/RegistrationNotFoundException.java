package com.events.exceptions;

public class RegistrationNotFoundException extends RuntimeException {
    public RegistrationNotFoundException(Long eventId, Long userId) {
        super("User " + userId + " is not registered to event " + eventId);
    }
}