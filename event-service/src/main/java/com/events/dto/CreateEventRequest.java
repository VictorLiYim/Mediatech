package com.events.dto;

import java.time.LocalDateTime;

import com.events.models.EventType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corps de la requête {@code POST /events}. */
public record CreateEventRequest(
        @NotBlank String title,
        String description,
        @NotNull @Future LocalDateTime eventDate,
        @NotNull EventType type,
        Long authorId,
        @Min(1) int capacity
) {
}