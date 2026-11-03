import java.time.LocalDate;

public record Loan(String isbn, int memberId, LocalDate borrowedOn, LocalDate dueOn, LocalDate returnedOn) {
    public boolean isActive() { return returnedOn == null; }

    public boolean isOverdue(LocalDate today) { return isActive() && today.isAfter(dueOn); }

    public Loan returnedOn(LocalDate date) { return new Loan(isbn, memberId, borrowedOn, dueOn, date); }
}
