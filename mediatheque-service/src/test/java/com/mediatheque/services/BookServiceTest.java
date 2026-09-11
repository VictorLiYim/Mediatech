package com.mediatheque.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediatheque.exceptions.BookAlreadyExistsException;
import com.mediatheque.exceptions.BookNotAvailableException;
import com.mediatheque.exceptions.BookNotFoundException;
import com.mediatheque.models.Author;
import com.mediatheque.models.Book;
import com.mediatheque.models.BookType;
import com.mediatheque.models.Genre;
import com.mediatheque.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private AuthorService authorService;

	@InjectMocks
	private BookServiceImpl bookService;

	private Book bookWithCopies(int total, int available) {
		Book book = new Book("Les Misérables", Set.of(new Author("Victor Hugo", "bio")),
				"978-2-07-040969-3", BookType.ROMAN, "desc", Set.of(Genre.CLASSIQUE), total);
		book.setAvailableCopies(available);
		return book;
	}

	@Test
	void createBook_whenIsbnFree_savesBook() {
		Author author = new Author("Victor Hugo", "bio");
		when(bookRepository.existsByIsbn("978-2-07-040969-3")).thenReturn(false);
		when(authorService.getByIds(Set.of(1L))).thenReturn(Set.of(author));
		when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Book created = bookService.createBook("Les Misérables", Set.of(1L), "978-2-07-040969-3",
				BookType.ROMAN, "desc", Set.of(Genre.CLASSIQUE), 3);

		assertThat(created.getTitle()).isEqualTo("Les Misérables");
		assertThat(created.getAvailableCopies()).isEqualTo(3);
	}

	@Test
	void createBook_whenIsbnAlreadyExists_throwsConflict() {
		when(bookRepository.existsByIsbn("978-2-07-040969-3")).thenReturn(true);

		assertThatThrownBy(() -> bookService.createBook("Les Misérables", Set.of(1L), "978-2-07-040969-3",
				BookType.ROMAN, "desc", Set.of(Genre.CLASSIQUE), 3))
				.isInstanceOf(BookAlreadyExistsException.class);
	}

	@Test
	void getById_whenMissing_throwsNotFound() {
		when(bookRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookService.getById(99L))
				.isInstanceOf(BookNotFoundException.class);
	}

	@Test
	void borrowCopy_whenAvailable_decrementsAvailableCopies() {
		Book book = bookWithCopies(3, 3);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		bookService.borrowCopy(1L);

		assertThat(book.getAvailableCopies()).isEqualTo(2);
		verify(bookRepository).save(book);
	}

	@Test
	void borrowCopy_whenNoCopyAvailable_throwsNotAvailable() {
		Book book = bookWithCopies(3, 0);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		assertThatThrownBy(() -> bookService.borrowCopy(1L))
				.isInstanceOf(BookNotAvailableException.class);
	}

	@Test
	void returnCopy_incrementsAvailableCopiesButClampsToTotal() {
		Book book = bookWithCopies(3, 3);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		bookService.returnCopy(1L);

		assertThat(book.getAvailableCopies()).isEqualTo(3);
	}

	@Test
	void updateAverageRating_setsAverageAndSaves() {
		Book book = bookWithCopies(3, 3);
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		bookService.updateAverageRating(1L, 4.5);

		assertThat(book.getAverageRating()).isEqualTo(4.5);
		verify(bookRepository).save(book);
	}
}
