package com.mediatheque.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediatheque.exceptions.BookNotCurrentlyBorrowedException;
import com.mediatheque.exceptions.UserNotFoundException;
import com.mediatheque.grpc.UserVerificationClient;
import com.mediatheque.models.Loan;
import com.mediatheque.repositories.LoanRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private BookService bookService;

	@Mock
	private UserVerificationClient userVerificationClient;

	@InjectMocks
	private LoanServiceImpl loanService;

	@Test
	void borrowBook_borrowsCopyAndSavesLoan() {
		when(userVerificationClient.userExists(42L)).thenReturn(true);
		when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Loan loan = loanService.borrowBook(1L, 42L);

		verify(bookService).borrowCopy(1L);
		assertThat(loan.getBookId()).isEqualTo(1L);
		assertThat(loan.getUserId()).isEqualTo(42L);
		assertThat(loan.getDueDate()).isEqualTo(LocalDate.now().plusWeeks(3));
	}

	@Test
	void borrowBook_whenUserDoesNotExist_throwsUserNotFoundAndNeverTouchesBook() {
		when(userVerificationClient.userExists(42L)).thenReturn(false);

		assertThatThrownBy(() -> loanService.borrowBook(1L, 42L))
				.isInstanceOf(UserNotFoundException.class);

		verify(bookService, never()).borrowCopy(any());
	}

	@Test
	void returnBook_whenActiveLoanExists_setsReturnDateAndReturnsCopy() {
		Loan loan = new Loan(1L, 42L, LocalDate.now().minusDays(1), LocalDate.now().plusWeeks(2));
		when(loanRepository.findByBookIdAndUserIdAndReturnDateIsNull(1L, 42L)).thenReturn(Optional.of(loan));
		when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Loan result = loanService.returnBook(1L, 42L);

		assertThat(result.getReturnDate()).isEqualTo(LocalDate.now());
		verify(bookService).returnCopy(1L);
	}

	@Test
	void returnBook_whenNoActiveLoan_throwsNotCurrentlyBorrowed() {
		when(loanRepository.findByBookIdAndUserIdAndReturnDateIsNull(1L, 42L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> loanService.returnBook(1L, 42L))
				.isInstanceOf(BookNotCurrentlyBorrowedException.class);
	}

	@Test
	void getLoansForUser_returnsRepositoryResult() {
		Loan loan = new Loan(1L, 42L, LocalDate.now(), LocalDate.now().plusWeeks(3));
		when(loanRepository.findByUserId(42L)).thenReturn(List.of(loan));

		List<Loan> result = loanService.getLoansForUser(42L);

		assertThat(result).containsExactly(loan);
	}
}
