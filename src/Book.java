import java.util.Objects;

class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private BookStatus bookStatus = BookStatus.AVAILABLE;

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return bookStatus == BookStatus.AVAILABLE;
    }

    public void markBorrowed() {
        bookStatus = BookStatus.BORROWED;
    }

    public void markReturned() {
        bookStatus = BookStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Book)) {
            return false;
        }

        Book other = (Book) obj;
        return Objects.equals(isbn, other.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format(
                "%s by %s [%s]", title, author, bookStatus
        );
    }
}
