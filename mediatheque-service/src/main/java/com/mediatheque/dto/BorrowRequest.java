package com.mediatheque.dto;
import jakarta.validation.constraints.NotNull;
public record BorrowRequest(@NotNull Long userId) {
}
