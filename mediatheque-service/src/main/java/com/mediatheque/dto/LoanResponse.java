package com.mediatheque.dto;

import java.time.LocalDate;
import com.mediatheque.models.Loan;

public record LoanResponse(
        Long id,
        Long bookId,
        Long userId,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(), loan.getBookId(), loan.getUserId(),
                loan.getBorrowDate(), loan.getDueDate(), loan.getReturnDate()
        );
    }
}

