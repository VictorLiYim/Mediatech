package com.mediatheque.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediatheque.exceptions.AuthorNotFoundException;
import com.mediatheque.models.Author;
import com.mediatheque.repositories.AuthorRepository;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

	@Mock
	private AuthorRepository authorRepository;

	@InjectMocks
	private AuthorServiceImpl authorService;

	@Test
	void createAuthor_savesAndReturnsAuthor() {
		when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Author created = authorService.createAuthor("Victor Hugo", "Écrivain français");

		assertThat(created.getName()).isEqualTo("Victor Hugo");
		assertThat(created.getBio()).isEqualTo("Écrivain français");
	}

	@Test
	void getById_whenMissing_throwsNotFound() {
		when(authorRepository.findById(42L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authorService.getById(42L))
				.isInstanceOf(AuthorNotFoundException.class);
	}

	@Test
	void getById_whenPresent_returnsAuthor() {
		Author stored = new Author("Victor Hugo", "bio");
		when(authorRepository.findById(1L)).thenReturn(Optional.of(stored));

		Author result = authorService.getById(1L);

		assertThat(result).isSameAs(stored);
	}

	@Test
	void getByIds_whenOneMissing_throwsNotFound() {
		when(authorRepository.findById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authorService.getByIds(Set.of(2L)))
				.isInstanceOf(AuthorNotFoundException.class);
	}

	@Test
	void getByIds_whenAllPresent_returnsAllAuthors() {
		Author hugo = new Author("Victor Hugo", "bio1");
		Author zola = new Author("Émile Zola", "bio2");
		when(authorRepository.findById(1L)).thenReturn(Optional.of(hugo));
		when(authorRepository.findById(2L)).thenReturn(Optional.of(zola));

		Set<Author> result = authorService.getByIds(Set.of(1L, 2L));

		assertThat(result).containsExactlyInAnyOrder(hugo, zola);
	}
}
