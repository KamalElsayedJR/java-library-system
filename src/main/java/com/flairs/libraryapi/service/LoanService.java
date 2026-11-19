package com.flairs.libraryapi.service;

import com.flairs.libraryapi.config.LibraryProperties;
import com.flairs.libraryapi.dto.BorrowRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.model.Loan;
import com.flairs.libraryapi.model.Member;
import com.flairs.libraryapi.repository.BookRepository;
import com.flairs.libraryapi.repository.LoanRepository;
import com.flairs.libraryapi.repository.MemberRepository;
import java.time.Clock;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // TODO 5: borrow a book. Everything below runs in ONE transaction.
    //  1. load the book and the member (404 if missing)
    //  2. 409 if every copy is already out   (loans.countByBookIdAndReturnedAtIsNull >= book.getCopies())
    //  3. 409 if the member already has props.maxActiveLoans() active loans
    //  4. due date = today (use the clock) + props.loanDays()
    //  5. save the Loan, log it, return the response

    @Transactional
    public LoanResponse borrow(BorrowRequest request) {

        // 1. Load book and member
        Book book = books.findById(request.bookId())
                .orElseThrow(() ->
                        new NotFoundException("Book", request.bookId()));

        Member member = members.findById(request.memberId())
                .orElseThrow(() ->
                        new NotFoundException("Member", request.memberId()));

        // 2. Check available copies
        long activeBookLoans =
                loans.countByBookIdAndReturnedAtIsNull(book.getId());

        if (activeBookLoans >= book.getCopies()) {
            throw new ConflictException(
                    "No available copies for book " + book.getId());
        }

        // 3. Check member's active loans limit
        long activeMemberLoans =
                loans.countByMemberIdAndReturnedAtIsNull(member.getId());

        if (activeMemberLoans >= props.maxActiveLoans()) {
            throw new ConflictException(
                    "Member has reached the maximum active loans limit");
        }

        // 4. Calculate due date using Clock
        LocalDate dueDate = LocalDate.now(clock)
                .plusDays(props.loanDays());

        // 5. Create and save loan
        Loan loan = new Loan(book, member, dueDate);

        Loan savedLoan = loans.save(loan);

        log.info(
                "Book borrowed successfully. Loan ID: {}, Book ID: {}, Member ID: {}",
                savedLoan.getId(),
                book.getId(),
                member.getId()
        );

        return Mapper.toResponse(savedLoan);
    }

    // TODO 6: return a book: 404 if the loan is missing, 409 if it was already returned,
    // otherwise loan.markReturned(). Do you need loans.save(...)? Why not?
    @Transactional
    public LoanResponse giveBack(Long loanId) {

        // 1. Find loan
        Loan loan = loans.findById(loanId)
                .orElseThrow(() ->
                        new NotFoundException("Loan", loanId));

        // 2. Check if already returned
        if (!loan.isActive()) {
            throw new ConflictException(
                    "This loan has already been returned");
        }

        // 3. Mark loan as returned
        loan.markReturned();

        // 4. Return response
        return Mapper.toResponse(loan);
    }
}
