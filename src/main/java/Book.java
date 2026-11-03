public record Book(String isbn, String title, String author, int year, String genre) {
    public Book {
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("isbn is required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title is required");
        if (year < 0) throw new IllegalArgumentException("year must not be negative");
    }
}
