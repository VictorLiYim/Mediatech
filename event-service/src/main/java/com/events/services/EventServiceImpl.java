package com.events.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.events.exceptions.EventNotFoundException;
import com.events.models.Event;
import com.events.models.EventType;
import com.events.repositories.EventRepository;

@Service
public class EventServiceImpl implements EventService{
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    public EventServiceImpl(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }
    @Override
    @Transactional
    public Event createEvent(String title, String description, LocalDateTime eventDate, EventType type,
                             Long authorId, int capacity){
        log.debug("Creating event title={} type={}", title, type);
        Event event = new Event(title, description, eventDate, type, authorId, capacity);
        Event saved = eventRepository.save(event);
        log.info("Created event id={} title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEvents(){
        List<Event> events = new ArrayList<>();
        eventRepository.findAll().forEach(events::add);
        return events;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getById(Long id){
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }
}
