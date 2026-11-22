package com.flairs.libraryapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

// A Loan links a Member and a Book: two many-to-one relations.
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "borrowed_at", nullable = false, updatable = false)
    private Instant borrowedAt = Instant.now();

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_at")
    private Instant returnedAt;

    protected Loan() {
    }

    public Loan(Book book, Member member, LocalDate dueDate) {
        this.book = book;
        this.member = member;
        this.dueDate = dueDate;
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    public void markReturned() {
        this.returnedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public Instant getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }
}
