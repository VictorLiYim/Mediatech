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

import com.mediatheque.dto.BookResponse;
import com.mediatheque.dto.CreateBookRequest;
import com.mediatheque.models.Book;
import com.mediatheque.services.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        log.info("POST /books isbn={}", request.isbn());
        Book book = bookService.createBook(
                request.title(), request.authorIds(), request.isbn(), request.type(),
                request.description(), request.genres(), request.totalCopies()
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(book.getId()).toUri();
        return ResponseEntity.created(location).body(BookResponse.from(book));
    }

    @GetMapping
    public List<BookResponse> getAllBooks() {
        log.info("GET /books");
        return bookService.getAllBooks().stream().map(BookResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        log.info("GET /books/{}", id);
        return BookResponse.from(bookService.getById(id));
    }
}
