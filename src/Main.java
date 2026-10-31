import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();

        library.addMember(new Member(1, "Mona", MemberType.STUDENT));
        library.addMember(new Member(2, "Ahmed", MemberType.STAFF));

        try {
            library.addBook(new Book("222", "Effective Java", "Joshua Bloch"));
            library.addBook(new Book("333", "Clean Code", "Robert C. Martin"));
            library.addBook(new Book("444", "Java: The Complete Reference", "Herbert Schildt"));
        } catch (DuplicateBookException e) {
            System.out.println("Error: " + e.getMessage());
        }

        Scanner in = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== Library System =====");
            System.out.println("1) List books");
            System.out.println("2) Search");
            System.out.println("3) Add book");
            System.out.println("4) Borrow");
            System.out.println("5) Return");
            System.out.println("6) Overdue");
            System.out.println("0) Exit");
            System.out.print("Choice: ");

            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        for (Book book : library.allBooks()) {
                            System.out.println(book);
                        }
                        break;

                    case "2":
                        System.out.print("Search title or author: ");
                        String text = in.nextLine().trim();

                        List<Book> results = library.search(text);
                        if (results.isEmpty()) {
                            System.out.println("No matching books found.");
                        } else {
                            results.forEach(System.out::println);
                        }
                        break;

                    case "3":
                        System.out.print("ISBN: ");
                        String isbn = in.nextLine().trim();

                        System.out.print("Title: ");
                        String title = in.nextLine().trim();

                        System.out.print("Author: ");
                        String author = in.nextLine().trim();

                        library.addBook(new Book(isbn, title, author));
                        System.out.println("Book added successfully.");
                        break;

                    case "4":
                        System.out.print("ISBN: ");
                        String borrowIsbn = in.nextLine().trim();

                        System.out.print("Member id: ");
                        int memberId = Integer.parseInt(in.nextLine().trim());

                        Loan loan = library.borrow(
                                borrowIsbn, memberId, LocalDate.now()
                        );

                        System.out.println("Borrowed: " + loan);
                        break;

                    case "5":
                        System.out.print("ISBN: ");
                        String returnIsbn = in.nextLine().trim();

                        library.returnBook(returnIsbn);
                        System.out.println("Book returned successfully.");
                        break;

                    case "6":
                        List<Loan> overdueLoans =
                                library.overdue(LocalDate.now());

                        if (overdueLoans.isEmpty()) {
                            System.out.println("No overdue loans.");
                        } else {
                            overdueLoans.forEach(System.out::println);
                        }
                        break;

                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (LibraryException | NumberFormatException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        in.close();
    }

}


