package com.mediatheque.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediatheque.exceptions.BookAlreadyExistsException;
import com.mediatheque.exceptions.BookNotAvailableException;
import com.mediatheque.exceptions.BookNotFoundException;
import com.mediatheque.models.Author;
import com.mediatheque.models.Book;
import com.mediatheque.models.BookType;
import com.mediatheque.models.Genre;
import com.mediatheque.repositories.BookRepository;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    @Override
    @Transactional
    public Book createBook(String title, Set<Long> authorIds, String isbn, BookType type,
                           String description, Set<Genre> genres, int totalCopies) {
        log.debug("Creating book isbn={}", isbn);
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BookAlreadyExistsException(isbn);
        }
        Set<Author> authors = authorService.getByIds(authorIds); // lève AuthorNotFoundException si besoin
        Book book = new Book(title, authors, isbn, type, description, genres, totalCopies);
        Book saved = bookRepository.save(book);
        log.info("Created book id={} isbn={}", saved.getId(), saved.getIsbn());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    @Transactional
    public void updateAverageRating(Long bookId, double newAverage) {
        Book book = getById(bookId);
        book.setAverageRating(newAverage);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void borrowCopy(Long bookId) {
        Book book = getById(bookId);
        if (!book.hasAvailableCopy()) {throw new BookNotAvailableException(bookId);}
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void returnCopy(Long bookId) {
        Book book = getById(bookId);
        int updated = Math.min(book.getAvailableCopies() + 1, book.getTotalCopies());
        book.setAvailableCopies(updated);
        bookRepository.save(book);
    }
}