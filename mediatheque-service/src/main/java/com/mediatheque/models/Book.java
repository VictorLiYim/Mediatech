package com.mediatheque.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "books",
        uniqueConstraints = @UniqueConstraint(name = "books_isbn", columnNames = "isbn")
)
@Getter
@Setter
@NoArgsConstructor
public class Book{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();
    @Column(nullable = false)
    private String isbn;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookType type; //Roman, Bd, etc...
    @Column(length = 2000)
    private String description;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Set<Genre> genres = new HashSet<>();
    @Column(name = "total_copies", nullable = false)
    private int totalCopies;
    @Column(name = "available_copies", nullable = false)
    private int availableCopies;
    @Column(name = "average_rating")
    private double averageRating;

    public Book(String title, Set<Author> authors, String isbn, BookType type,
                String description, Set<Genre> genres, int totalCopies) {
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.type = type;
        this.description = description;
        this.genres = genres;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public boolean hasAvailableCopy() {
        return availableCopies > 0;
    }
}