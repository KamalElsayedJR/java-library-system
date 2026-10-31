import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<Integer, Member> members = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();

    public void addBook(Book book) {
        if (books.putIfAbsent(book.isbn(), book) != null) {
            throw new LibraryException("A book with ISBN " + book.isbn() + " already exists");
        }
    }

    public void addMember(Member member) {
        members.put(member.id(), member);
    }

    public Optional<Book> findBook(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    // TODO: every method below. Use streams, Optional and records. The tests in LibraryTest describe the behaviour.

    // All books sorted by title.
    public List<Book> allBooksByTitle() {
        return books.values().stream()
                .sorted(Comparator.comparing(Book::title))
                .toList();
    }

    // Books whose title or author contains the text, ignoring case, sorted by title.
    public List<Book> search(String text) {
        String needle = text.toLowerCase();
        return allBooksByTitle().stream()
                .filter(book -> book.title().toLowerCase().contains(needle)
                        || book.author().toLowerCase().contains(needle))
                .toList();
    }

    // Author -> titles (sorted). Authors in alphabetical order (TreeMap::new).
    public Map<String, List<String>> titlesByAuthor() {
        return books.values().stream()
                .collect(Collectors.groupingBy(Book::author, TreeMap::new,
                        Collectors.mapping(Book::title,
                                Collectors.collectingAndThen(Collectors.toList(),
                                        titles -> titles.stream().sorted().toList()))));
    }

    // Genre -> number of books. Genres in alphabetical order.
    public Map<String, Long> countByGenre() {
        return books.values().stream()
                .collect(Collectors.groupingBy(Book::genre, TreeMap::new, Collectors.counting()));
    }

    // The n oldest books, oldest first (ties by title).
    public List<Book> oldestBooks(int n) {
        return books.values().stream()
                .sorted(Comparator.comparingInt(Book::year).thenComparing(Book::title))
                .limit(n)
                .toList();
    }

    // True if no active loan exists for this ISBN.
    public boolean isAvailable(String isbn) {
        return loans.stream().noneMatch(loan -> loan.isActive() && loan.isbn().equals(isbn));
    }

    public long activeLoans(int memberId) {
        return loans.stream()
                .filter(loan -> loan.isActive() && loan.memberId() == memberId)
                .count();
    }

    // Throws LibraryException when: the book or member does not exist ("No book with ISBN 999"),
    // the book is already borrowed ("Dune is already borrowed"), or the member is at the limit.
    // Due date = today + member.type().loanDays().
    public Loan borrow(String isbn, int memberId, LocalDate today) {
        Book book = findBook(isbn)
                .orElseThrow(() -> new LibraryException("No book with ISBN " + isbn));
        Member member = Optional.ofNullable(members.get(memberId))
                .orElseThrow(() -> new LibraryException("No member with id " + memberId));

        if (!isAvailable(isbn)) {
            throw new LibraryException(book.title() + " is already borrowed");
        }

        if (activeLoans(memberId) >= member.type().maxLoans()) {
            throw new LibraryException(member.name() + " reached the limit of "
                    + member.type().maxLoans() + " books");
        }

        Loan loan = new Loan(isbn, memberId, today,
                today.plusDays(member.type().loanDays()), null);
        loans.add(loan);
        return loan;
    }

    // Replaces the active loan with a returned copy. Throws LibraryException if the book is not on loan.
    public void returnBook(String isbn, LocalDate today) {
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            if (loan.isActive() && loan.isbn().equals(isbn)) {
                loans.set(i, loan.returnedOn(today));
                return;
            }
        }
        throw new LibraryException("Book " + isbn + " is not on loan");
    }

    // Active loans whose due date is before today.
    public List<Loan> overdue(LocalDate today) {
        return loans.stream()
                .filter(loan -> loan.isOverdue(today))
                .toList();
    }
}
