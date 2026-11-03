import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @Test
    void shouldCreateActiveLoan() {
        LocalDate today = LocalDate.of(2026, 11, 3);

        Loan loan = new Loan("123", 1, today, today.plusDays(14), null);

        assertTrue(loan.isActive());
    }

    @Test
    void shouldReturnFalseWhenLoanIsNotActive() {
        LocalDate today = LocalDate.of(2026, 11, 3);

        Loan loan = new Loan("123", 1, today, today.plusDays(14), today);

        assertFalse(loan.isActive());
    }

    @Test
    void shouldBeOverdueWhenTodayIsAfterDueDate() {
        LocalDate today = LocalDate.of(2026, 11, 3);

        Loan loan = new Loan(
                "123", 1,
                today.minusDays(20),
                today.minusDays(6),
                null
        );

        assertTrue(loan.isOverdue(today));
    }

    @Test
    void shouldNotBeOverdueWhenTodayEqualsDueDate() {
        LocalDate today = LocalDate.of(2026, 11, 3);

        Loan loan = new Loan(
                "123", 1,
                today.minusDays(14),
                today,
                null
        );

        assertFalse(loan.isOverdue(today));
    }

    @Test
    void shouldNotBeOverdueWhenLoanIsReturned() {
        LocalDate today = LocalDate.of(2026, 11, 3);

        Loan loan = new Loan(
                "123", 1,
                today.minusDays(20),
                today.minusDays(6),
                today.minusDays(2)
        );

        assertFalse(loan.isOverdue(today));
    }

    @Test
    void returnedOnShouldCreateNewLoanWithReturnDate() {
        LocalDate borrowed = LocalDate.of(2026, 10, 1);
        LocalDate due = borrowed.plusDays(14);
        LocalDate returned = LocalDate.of(2026, 10, 10);

        Loan loan = new Loan("123", 1, borrowed, due, null);

        Loan returnedLoan = loan.returnedOn(returned);

        assertEquals(returned, returnedLoan.returnedOn());
        assertFalse(returnedLoan.isActive());
        assertTrue(loan.isActive());
    }
}
