package com.mediatheque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAuthorRequest(
        @NotBlank String name,
        String bio,
        @NotNull Long userId
) {
}
