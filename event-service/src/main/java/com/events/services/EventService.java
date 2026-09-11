package com.events.services;

import com.events.models.Event;
import com.events.models.EventType;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event createEvent(String title, String description, LocalDateTime eventDate, EventType type,
                      Long authorId, int capacity);

    List<Event> getAllEvents();

    Event getById(Long id);
}
