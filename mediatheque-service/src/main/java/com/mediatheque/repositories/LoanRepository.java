package com.mediatheque.repositories;

import com.mediatheque.models.Loan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends CrudRepository<Loan, Long> {
    Optional<Loan> findByBookIdAndUserIdAndReturnDateIsNull(Long bookId, Long userId);
    List<Loan> findByUserId(Long userId);
}
