package com.flairs.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.flairs.libraryapi.config.LibraryProperties;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Author;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.model.Loan;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.model.Role;
import com.flairs.libraryapi.repository.BookRepository;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import com.flairs.libraryapi.security.CurrentMember;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LoanReturnTest {

    static final CurrentMember OMAR = new CurrentMember(2L, "omar@example.com", Role.MEMBER);
    static final CurrentMember SARA = new CurrentMember(1L, "sara@example.com", Role.MEMBER);
    static final CurrentMember LIBRARIAN = new CurrentMember(3L, "librarian@example.com", Role.LIBRARIAN);

    @Mock LoanRepository loans;
    @Mock BookRepository books;
    @Mock MemberRepository members;

    LoanService service;

    @BeforeEach
    void setUp() {
        service = new LoanService(loans, books, members, new LibraryProperties(3, 14), Clock.systemUTC());
        Member omar = new Member("Omar", "omar@example.com", "hash");
        ReflectionTestUtils.setField(omar, "id", 2L);
        Loan loan = new Loan(new Book("Dune", "9780441013593", 1965, 1, new Author("Frank Herbert")),
                omar, LocalDate.now().plusDays(14));
        when(loans.findById(5L)).thenReturn(Optional.of(loan));
    }

    @Test
    void returnMarksLoanAndSecondReturnFails() {
        assertThat(service.giveBack(5L, OMAR).returnedAt()).isNotNull();
        assertThatThrownBy(() -> service.giveBack(5L, OMAR)).isInstanceOf(ConflictException.class);
    }

    @Test
    void anotherMemberGetsNotFound() {
        assertThatThrownBy(() -> service.giveBack(5L, SARA)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void aLibrarianCanReturnAnyLoan() {
        assertThat(service.giveBack(5L, LIBRARIAN).returnedAt()).isNotNull();
    }
}
