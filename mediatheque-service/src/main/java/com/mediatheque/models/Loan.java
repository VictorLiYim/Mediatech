package com.mediatheque.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "book_id", nullable = false)
    private Long bookId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(name = "return_date")
    private LocalDate returnDate;
    public Loan(Long bookId, Long userId, LocalDate borrowDate, LocalDate dueDate){
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
}
