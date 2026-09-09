package com.mediatheque.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mediatheque.models.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    Author findByName(String name);
}