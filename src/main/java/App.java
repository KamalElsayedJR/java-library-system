import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        Library library = new Library();
        library.addBook(new Book("111", "Dune", "Frank Herbert", 1965, "Sci-Fi"));
        library.addBook(new Book("222", "Emma", "Jane Austen", 1815, "Classic"));
        library.addBook(new Book("333", "Persuasion", "Jane Austen", 1817, "Classic"));
        library.addMember(new Member(1, "Sara", MemberType.STUDENT));

        library.borrow("111", 1, LocalDate.of(2026, 1, 1));
        System.out.println(library.allBooksByTitle());
        System.out.println(library.titlesByAuthor());
        System.out.println(library.countByGenre());
        System.out.println("overdue: " + library.overdue(LocalDate.of(2026, 2, 1)));
    }
}
