package com.flairs.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flairs.libraryapi.config.LibraryProperties;
import com.flairs.libraryapi.dto.BorrowRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.exception.BadRequestException;
import com.flairs.libraryapi.exception.ConflictException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)     // not every test uses every stub from setUp
class LoanServiceTest {

    @Mock LoanRepository loans;
    @Mock BookRepository books;
    @Mock MemberRepository members;

    static final CurrentMember SARA = new CurrentMember(1L, "sara@example.com", Role.MEMBER);
    static final CurrentMember LIBRARIAN = new CurrentMember(3L, "librarian@example.com", Role.LIBRARIAN);

    LoanService service;
    Book book;
    Member member;

    @BeforeEach
    void setUp() {
        Clock fixed = Clock.fixed(Instant.parse("2026-03-01T10:00:00Z"), ZoneOffset.UTC);
        service = new LoanService(loans, books, members, new LibraryProperties(3, 14), fixed);
        book = new Book("1984", "9780451524935", 1949, 2, new Author("George Orwell"));
        ReflectionTestUtils.setField(book, "id", 1L);
        member = new Member("Sara", "sara@example.com", "hash");
        ReflectionTestUtils.setField(member, "id", 1L);
        when(books.findById(1L)).thenReturn(Optional.of(book));
        when(members.findById(1L)).thenReturn(Optional.of(member));
    }

    @Test
    void borrowSetsDueDateFromLoanDays() {
        when(loans.countByBookIdAndReturnedAtIsNull(1L)).thenReturn(0L);
        when(loans.countByMemberIdAndReturnedAtIsNull(1L)).thenReturn(0L);
        when(loans.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse loan = service.borrow(new BorrowRequest(1L, 1L), SARA);

        assertThat(loan.dueDate()).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(loan.returnedAt()).isNull();
    }

    @Test
    void borrowFailsWhenAllCopiesAreOut() {
        when(loans.countByBookIdAndReturnedAtIsNull(1L)).thenReturn(2L);

        assertThatThrownBy(() -> service.borrow(new BorrowRequest(1L, 1L), SARA))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No copies");
        verify(loans, never()).save(any());
    }

    @Test
    void borrowFailsWhenMemberHasMaxLoans() {
        when(loans.countByBookIdAndReturnedAtIsNull(1L)).thenReturn(0L);
        when(loans.countByMemberIdAndReturnedAtIsNull(1L)).thenReturn(3L);

        assertThatThrownBy(() -> service.borrow(new BorrowRequest(1L, 1L), SARA))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("max 3");
    }

    // ---- Session 12: who may borrow for whom ----

    @Test
    void aMemberBorrowsForThemselvesWhenMemberIdIsLeftOut() {
        when(loans.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse loan = service.borrow(new BorrowRequest(1L, null), SARA);

        assertThat(loan.memberId()).isEqualTo(1L);
    }

    @Test
    void aMemberCannotBorrowForSomeoneElse() {
        assertThatThrownBy(() -> service.borrow(new BorrowRequest(1L, 2L), SARA))
                .isInstanceOf(AccessDeniedException.class);
        verify(loans, never()).save(any());
    }

    @Test
    void aLibrarianBorrowsForTheMemberInTheRequest() {
        when(loans.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.borrow(new BorrowRequest(1L, 1L), LIBRARIAN).memberId()).isEqualTo(1L);
    }

    @Test
    void aLibrarianMustSayForWhom() {
        assertThatThrownBy(() -> service.borrow(new BorrowRequest(1L, null), LIBRARIAN))
                .isInstanceOf(BadRequestException.class);
    }
}
