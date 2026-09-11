package com.events.dto;

import java.time.LocalDateTime;

import com.events.models.Event;
import com.events.models.EventType;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime eventDate,
        EventType type,
        Long authorId,
        int capacity
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getType(),
                event.getAuthorId(),
                event.getCapacity()
        );
    }
}