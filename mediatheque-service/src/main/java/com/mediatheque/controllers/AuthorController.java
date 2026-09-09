package com.mediatheque.controllers;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mediatheque.dto.AuthorResponse;
import com.mediatheque.dto.CreateAuthorRequest;
import com.mediatheque.models.Author;
import com.mediatheque.services.AuthorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        log.info("POST /authors name={}", request.name());
        Author author = authorService.createAuthor(request.name(), request.bio());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(author.getId()).toUri();
        return ResponseEntity.created(location).body(AuthorResponse.from(author));
    }

    @GetMapping
    public List<AuthorResponse> getAllAuthors() {
        log.info("GET /authors");
        return authorService.getAllAuthors().stream().map(AuthorResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AuthorResponse getAuthor(@PathVariable Long id) {
        log.info("GET /authors/{}", id);
        return AuthorResponse.from(authorService.getById(id));
    }
}
