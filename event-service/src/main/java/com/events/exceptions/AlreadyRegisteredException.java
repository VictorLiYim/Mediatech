package com.events.exceptions;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(Long eventId, Long userId) {
        super("User " + userId + " is already registered to event " + eventId);
    }
}
