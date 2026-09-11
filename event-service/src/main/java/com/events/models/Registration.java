package com.events.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="registrations")
@Getter
@Setter
@NoArgsConstructor
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long eventId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Instant registeredAt;
    public Registration(Long eventId, Long userId){
        this.eventId = eventId;
        this.userId = userId;
        this.registeredAt = Instant.now();
    }
}
