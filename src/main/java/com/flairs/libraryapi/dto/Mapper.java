package com.flairs.libraryapi.dto;

import com.flairs.libraryapi.model.Author;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.model.Loan;
import com.flairs.libraryapi.model.Member;

public final class Mapper {

    private Mapper() {
    }

    public static AuthorResponse toResponse(Author a) {
        return new AuthorResponse(a.getId(), a.getName());
    }

    public static BookResponse toResponse(Book b) {
        return new BookResponse(b.getId(), b.getTitle(), b.getIsbn(), b.getYear(), b.getCopies(),
                b.getAuthor().getId(), b.getAuthor().getName());
    }

    public static MemberResponse toResponse(Member m) {
        return new MemberResponse(m.getId(), m.getName(), m.getEmail(), m.getJoinedAt());
    }

    public static LoanResponse toResponse(Loan l) {
        return new LoanResponse(l.getId(), l.getBook().getId(), l.getBook().getTitle(), l.getMember().getId(),
                l.getMember().getName(), l.getBorrowedAt(), l.getDueDate(), l.getReturnedAt());
    }
}
