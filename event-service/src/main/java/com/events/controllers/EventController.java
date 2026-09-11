package com.events.controllers;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.events.dto.CreateEventRequest;
import com.events.dto.EventResponse;
import com.events.models.Event;
import com.events.services.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        log.info("POST /events title={}", request.title());
        Event event = eventService.createEvent(
                request.title(), request.description(), request.eventDate(),
                request.type(), request.authorId(), request.capacity()
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(event.getId()).toUri();
        return ResponseEntity.created(location).body(EventResponse.from(event));
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        log.info("GET /events");
        return eventService.getAllEvents().stream().map(EventResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable Long id) {
        log.info("GET /events/{}", id);
        return EventResponse.from(eventService.getById(id));
    }
}