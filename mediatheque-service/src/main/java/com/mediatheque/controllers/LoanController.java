package com.mediatheque.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mediatheque.dto.BorrowRequest;
import com.mediatheque.dto.LoanResponse;
import com.mediatheque.dto.ReturnRequest;
import com.mediatheque.services.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PutMapping("/{id}/borrow")
    public LoanResponse borrow(@PathVariable Long id, @Valid @RequestBody BorrowRequest request) {
        log.info("PUT /books/{}/borrow userId={}", id, request.userId());
        return LoanResponse.from(loanService.borrowBook(id, request.userId()));
    }

    @PutMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id, @Valid @RequestBody ReturnRequest request) {
        log.info("PUT /books/{}/return userId={}", id, request.userId());
        return LoanResponse.from(loanService.returnBook(id, request.userId()));
    }

    @GetMapping("/loans")
    public List<LoanResponse> getLoansForUser(@RequestParam Long userId) {
        log.info("GET /books/loans userId={}", userId);
        return loanService.getLoansForUser(userId).stream().map(LoanResponse::from).toList();
    }
}
