package com.events.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="events")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length= 1000)
    private String description;
    @Column(nullable = false)
    private LocalDateTime eventDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType type;
    @Column(nullable = false)
    private Long authorId;
    @Column(nullable = false)
    private int capacity;

    public Event(String title, String description, LocalDateTime eventDate, EventType type, Long authorId, int capacity){
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.type = type;
        this.authorId = authorId;
        this.capacity = capacity;
    }
}
