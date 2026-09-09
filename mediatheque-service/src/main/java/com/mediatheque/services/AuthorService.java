package com.mediatheque.services;

import java.util.List;
import java.util.Set;

import com.mediatheque.models.Author;

public interface AuthorService {
    Author createAuthor(String name, String bio);
    List<Author> getAllAuthors();
    Author getById(Long id);
    Set<Author> getByIds(Set<Long> ids);
}