package com.mediatheque.services;

import java.util.List;

import com.mediatheque.models.Loan;

public interface LoanService {

    Loan borrowBook(Long bookId, Long userId);

    Loan returnBook(Long bookId, Long userId);

    List<Loan> getLoansForUser(Long userId);
}
