package com.events.dto;

import jakarta.validation.constraints.NotNull;

/** Corps de la requête {@code POST /events/{id}/register}. */
public record RegisterRequest(
        @NotNull Long userId
) {
}