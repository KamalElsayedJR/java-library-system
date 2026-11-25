package com.flairs.libraryapi.service;

import com.flairs.libraryapi.config.LibraryProperties;
import com.flairs.libraryapi.dto.BorrowRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.exception.BadRequestException;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.model.Loan;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.repository.BookRepository;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import com.flairs.libraryapi.security.CurrentMember;
import java.time.Clock;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);

    private final LoanRepository loans;
    private final BookRepository books;
    private final MemberRepository members;
    private final LibraryProperties props;
    private final Clock clock;

    public LoanService(LoanRepository loans, BookRepository books, MemberRepository members,
                       LibraryProperties props, Clock clock) {
        this.loans = loans;
        this.books = books;
        this.members = members;
        this.props = props;
        this.clock = clock;
    }

    // Three reads and one insert in ONE transaction: all or nothing.
    @Transactional
    public LoanResponse borrow(BorrowRequest request, CurrentMember caller) {
        Long memberId = borrowerId(request, caller);
        Book book = books.findById(request.bookId())
                .orElseThrow(() -> new NotFoundException("Book", request.bookId()));
        Member member = members.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member", memberId));

        long onLoan = loans.countByBookIdAndReturnedAtIsNull(book.getId());
        if (onLoan >= book.getCopies()) {
            throw new ConflictException("No copies of '" + book.getTitle() + "' are available");
        }
        long active = loans.countByMemberIdAndReturnedAtIsNull(member.getId());
        if (active >= props.maxActiveLoans()) {
            throw new ConflictException(member.getName() + " already has " + active + " active loans (max "
                    + props.maxActiveLoans() + ")");
        }

        LocalDate due = LocalDate.now(clock).plusDays(props.loanDays());
        Loan loan = loans.save(new Loan(book, member, due));
        log.info("Member {} borrowed book {} until {}", member.getId(), book.getId(), due);
        return Mapper.toResponse(loan);
    }

    // A member may only return their own loans. Someone else's loan is "not found" for them.
    @Transactional
    public LoanResponse giveBack(Long loanId, CurrentMember caller) {
        Loan loan = loans.findById(loanId)
                .filter(l -> caller.canSee(l.getMember().getId()))
                .orElseThrow(() -> new NotFoundException("Loan", loanId));
        if (!loan.isActive()) {
            throw new ConflictException("Loan " + loanId + " was already returned");
        }
        loan.markReturned();
        log.info("Loan {} returned", loanId);
        return Mapper.toResponse(loan);
    }

    // A MEMBER borrows for themselves: memberId may be left out, or must be their own id.
    // A LIBRARIAN borrows for a member at the desk, so memberId is required.
    private Long borrowerId(BorrowRequest request, CurrentMember caller) {
        if (caller.isLibrarian()) {
            if (request.memberId() == null) {
                throw new BadRequestException("memberId is required when a librarian borrows for a member");
            }
            return request.memberId();
        }
        if (request.memberId() != null && !request.memberId().equals(caller.id())) {
            throw new AccessDeniedException("Members can only borrow for themselves");
        }
        return caller.id();
    }
}
