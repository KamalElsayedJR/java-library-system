package com.flairs.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.flairs.libraryapi.config.LibraryProperties;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.model.Author;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.model.Loan;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.repository.BookRepository;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@org.junit.jupiter.api.Disabled("Remove this line when TODO 5 and 6 are done")
class LoanReturnTest {

    @Mock LoanRepository loans;
    @Mock BookRepository books;
    @Mock MemberRepository members;

    @Test
    void returnMarksLoanAndSecondReturnFails() {
        LoanService service = new LoanService(loans, books, members, new LibraryProperties(3, 14), Clock.systemUTC());
        Loan loan = new Loan(new Book("Dune", "9780441013593", 1965, 1, new Author("Frank Herbert")),
                new Member("Omar", "omar@example.com"), LocalDate.now().plusDays(14));
        when(loans.findById(5L)).thenReturn(Optional.of(loan));

        assertThat(service.giveBack(5L).returnedAt()).isNotNull();
        assertThatThrownBy(() -> service.giveBack(5L)).isInstanceOf(ConflictException.class);
    }
}
