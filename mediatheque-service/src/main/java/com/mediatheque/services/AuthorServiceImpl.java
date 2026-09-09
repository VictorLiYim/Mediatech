package com.mediatheque.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediatheque.exceptions.AuthorNotFoundException;
import com.mediatheque.models.Author;
import com.mediatheque.repositories.AuthorRepository;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    public Author createAuthor(String name, String bio) {
        Author saved = authorRepository.save(new Author(name, bio));
        log.info("Created author id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        authorRepository.findAll().forEach(authors::add);
        return authors;
    }

    @Override
    @Transactional(readOnly = true)
    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Author> getByIds(Set<Long> ids) {
        return ids.stream().map(this::getById).collect(Collectors.toSet());
    }
}