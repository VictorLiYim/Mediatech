package com.mediatheque.dto;

import jakarta.validation.constraints.NotNull;

public record ReturnRequest(@NotNull Long userId) {
}
