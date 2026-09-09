package com.mediatheque.dto;
import com.mediatheque.models.Author;

public record AuthorResponse(Long id, String name, String bio) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(author.getId(), author.getName(), author.getBio());
    }
}
