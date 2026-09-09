package com.mediatheque.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAuthorRequest(
        @NotBlank String name,
        String bio
) {
}
