package com.mediatheque.dto;

import java.util.Set;

import com.mediatheque.models.BookType;
import com.mediatheque.models.Genre;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateBookRequest(
        @NotBlank String title,
        @NotEmpty Set<Long> authorIds,
        @NotBlank String isbn,
        @NotNull BookType type,
        String description,
        Set<Genre> genres,
        @Min(1) int totalCopies,
        @NotNull Long userId) {}
