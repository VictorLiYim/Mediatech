package com.events.exceptions;

public class EventFullException extends RuntimeException {
    public EventFullException(Long eventId) {
        super("Event "+ eventId + " has reached the max capacity");
    }
}
