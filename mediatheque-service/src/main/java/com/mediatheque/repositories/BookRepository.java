package com.mediatheque.repositories;

import com.mediatheque.models.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
    Book findByTitle(String title);
    boolean existsByIsbn(String isbn);
}
