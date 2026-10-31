import java.time.LocalDate;

class Loan {
    private final Book book;
    private final Member member;
    private final LocalDate borrowedOn;
    private final LocalDate dueOn;
    private boolean returned;

    public Loan(Book book, Member member, LocalDate borrowedOn) {
        this.book = book;
        this.member = member;
        this.borrowedOn = borrowedOn;
        this.dueOn = borrowedOn.plusDays(member.getType().loanDays());
        this.returned = false;

        book.markBorrowed();
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public LocalDate getDueOn() {
        return dueOn;
    }

    public boolean isReturned() {
        return returned;
    }

    public boolean isOverdue(LocalDate today) {
        return !returned && today.isAfter(dueOn);
    }

    public void returnBook() {
        if (!returned) {
            returned = true;
            book.markReturned();
        }
    }

    @Override
    public String toString() {
        return book.getTitle() + " -> "
                + member.getName() + ", due " + dueOn;
    }
}
