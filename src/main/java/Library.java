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

    public List<Book> allBooksByTitle() {
        return books.values().stream().sorted(Comparator.comparing(Book::title)).toList();
    }

    public List<Book> search(String text) {
        String needle = text.toLowerCase();
        return allBooksByTitle().stream()
                .filter(b -> b.title().toLowerCase().contains(needle) || b.author().toLowerCase().contains(needle))
                .toList();
    }

    public Map<String, List<String>> titlesByAuthor() {
        return books.values().stream()
                .collect(Collectors.groupingBy(Book::author, TreeMap::new,
                        Collectors.mapping(Book::title, Collectors.collectingAndThen(Collectors.toList(), l -> l.stream().sorted().toList()))));
    }

    public Map<String, Long> countByGenre() {
        return books.values().stream()
                .collect(Collectors.groupingBy(Book::genre, TreeMap::new, Collectors.counting()));
    }

    public List<Book> oldestBooks(int n) {
        return books.values().stream()
                .sorted(Comparator.comparingInt(Book::year).thenComparing(Book::title))
                .limit(n)
                .toList();
    }

    public boolean isAvailable(String isbn) {
        return loans.stream().noneMatch(l -> l.isActive() && l.isbn().equals(isbn));
    }

    public long activeLoans(int memberId) {
        return loans.stream().filter(l -> l.isActive() && l.memberId() == memberId).count();
    }

    public Loan borrow(String isbn, int memberId, LocalDate today) {
        Book book = findBook(isbn).orElseThrow(() -> new LibraryException("No book with ISBN " + isbn));
        Member member = Optional.ofNullable(members.get(memberId))
                .orElseThrow(() -> new LibraryException("No member with id " + memberId));
        if (!isAvailable(isbn)) {
            throw new LibraryException(book.title() + " is already borrowed");
        }
        if (activeLoans(memberId) >= member.type().maxLoans()) {
            throw new LibraryException(member.name() + " reached the limit of " + member.type().maxLoans() + " books");
        }
        Loan loan = new Loan(isbn, memberId, today, today.plusDays(member.type().loanDays()), null);
        loans.add(loan);
        return loan;
    }

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

    public List<Loan> overdue(LocalDate today) {
        return loans.stream().filter(l -> l.isOverdue(today)).toList();
    }
}
