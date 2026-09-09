package com.mediatheque.services;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediatheque.exceptions.BookNotCurrentlyBorrowedException;
import com.mediatheque.models.Loan;
import com.mediatheque.repositories.LoanRepository;

@Service
public class LoanServiceImpl implements LoanService {

    private static final int LOAN_DURATION_WEEKS = 3;

    private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanServiceImpl(LoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    @Override
    @Transactional
    public Loan borrowBook(Long bookId, Long userId) {
        log.debug("Borrow attempt bookId={} userId={}", bookId, userId);
        bookService.borrowCopy(bookId); // vérifie existence + dispo, lève l'exception sinon

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(bookId, userId, today, today.plusWeeks(LOAN_DURATION_WEEKS));
        Loan saved = loanRepository.save(loan);

        log.info("Book {} borrowed by user {}, due {}", bookId, userId, loan.getDueDate());
        return saved;
    }

    @Override
    @Transactional
    public Loan returnBook(Long bookId, Long userId) {
        log.debug("Return attempt bookId={} userId={}", bookId, userId);
        Loan loan = loanRepository.findByBookIdAndUserIdAndReturnDateIsNull(bookId, userId)
                .orElseThrow(() -> new BookNotCurrentlyBorrowedException(bookId, userId));

        loan.setReturnDate(LocalDate.now());
        Loan saved = loanRepository.save(loan);

        bookService.returnCopy(bookId);

        log.info("Book {} returned by user {} (loan id={})", bookId, userId, loan.getId());
        return saved;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Loan> getLoansForUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }
}
