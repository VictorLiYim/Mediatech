package com.events.dto;

import java.time.Instant;

import com.events.models.Registration;

public record RegistrationResponse(
        Long id,
        Long eventId,
        Long userId,
        Instant registeredAt
) {

    public static RegistrationResponse from(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getEventId(),
                registration.getUserId(),
                registration.getRegisteredAt()
        );
    }
}