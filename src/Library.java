import java.time.LocalDate;
import java.util.*;

class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<Integer, Member> members = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();

    public void addBook(Book book) throws DuplicateBookException {
        if (books.containsKey(book.getIsbn())) {
            throw new DuplicateBookException(
                    "Book with ISBN " + book.getIsbn() + " already exists"
            );
        }
        books.put(book.getIsbn(), book);
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    public Book findBook(String isbn) throws BookNotFoundException {
        Book book = books.get(isbn);

        if (book == null) {
            throw new BookNotFoundException("No book with ISBN " + isbn);
        }

        return book;
    }

    public Member findMember(int id) throws MemberNotFoundException {
        Member member = members.get(id);

        if (member == null) {
            throw new MemberNotFoundException(
                    "No member with id " + id
            );
        }

        return member;
    }

    public List<Book> allBooks() {
        List<Book> result = new ArrayList<>(books.values());
        result.sort(Comparator.comparing(
                Book::getTitle,
                String.CASE_INSENSITIVE_ORDER
        ));
        return result;
    }

    public List<Book> search(String text) {
        String query = text.toLowerCase();

        List<Book> result = new ArrayList<>();

        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(query)
                    || book.getAuthor().toLowerCase().contains(query)) {
                result.add(book);
            }
        }

        result.sort(Comparator.comparing(
                Book::getTitle,
                String.CASE_INSENSITIVE_ORDER
        ));

        return result;
    }

    public int activeLoans(Member member) {
        int count = 0;

        for (Loan loan : loans) {
            if (!loan.isReturned()
                    && loan.getMember().equals(member)) {
                count++;
            }
        }

        return count;
    }

    public Loan borrow(String isbn, int memberId, LocalDate today)
            throws LibraryException {

        Book book = findBook(isbn);
        Member member = findMember(memberId);

        if (!book.isAvailable()) {
            throw new BookUnavailableException(
                    book.getTitle() + " is already borrowed"
            );
        }

        if (!member.canBorrow(activeLoans(member))) {
            throw new LoanLimitException(
                    member.getName() + " has reached the loan limit"
            );
        }

        Loan loan = new Loan(book, member, today);
        loans.add(loan);

        return loan;
    }

    public void returnBook(String isbn) throws LibraryException {
        findBook(isbn);

        for (int i = loans.size() - 1; i >= 0; i--) {
            Loan loan = loans.get(i);

            if (loan.getBook().getIsbn().equals(isbn)
                    && !loan.isReturned()) {
                loan.returnBook();
                return;
            }
        }

        throw new BookUnavailableException(
                "No active loan found for ISBN " + isbn
        );
    }

    public List<Loan> overdue(LocalDate today) {
        List<Loan> result = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.isOverdue(today)) {
                result.add(loan);
            }
        }

        return result;
    }
}
