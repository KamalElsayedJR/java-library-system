import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void constructor_ShouldCreateBookSuccessfully() {
        Book book = new Book(
                "9780134685991",
                "Effective Java",
                "Joshua Bloch",
                2018,
                "Programming"
        );

        assertEquals("9780134685991", book.isbn());
        assertEquals("Effective Java", book.title());
        assertEquals("Joshua Bloch", book.author());
        assertEquals(2018, book.year());
        assertEquals("Programming", book.genre());
    }

    @Test
    void constructor_ShouldThrowException_WhenIsbnIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book(null, "Effective Java", "Joshua Bloch", 2018, "Programming")
        );
    }

    @Test
    void constructor_ShouldThrowException_WhenIsbnIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book(" ", "Effective Java", "Joshua Bloch", 2018, "Programming")
        );
    }

    @Test
    void constructor_ShouldThrowException_WhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book("123", null, "Joshua Bloch", 2018, "Programming")
        );
    }

    @Test
    void constructor_ShouldThrowException_WhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book("123", " ", "Joshua Bloch", 2018, "Programming")
        );
    }

    @Test
    void constructor_ShouldThrowException_WhenYearIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book("123", "Effective Java", "Joshua Bloch", -1, "Programming")
        );
    }

    @Test
    void constructor_ShouldAcceptZeroAsYear() {
        Book book = new Book("123", "Book", "Author", 0, "General");

        assertEquals(0, book.year());
    }

    @Test
    void equals_ShouldReturnTrue_WhenBooksHaveSameData() {
        Book first = new Book("123", "Book", "Author", 2020, "Programming");
        Book second = new Book("123", "Book", "Author", 2020, "Programming");

        assertEquals(first, second);
    }

    @Test
    void equals_ShouldReturnFalse_WhenBooksHaveDifferentData() {
        Book first = new Book("123", "Book", "Author", 2020, "Programming");
        Book second = new Book("456", "Another Book", "Author", 2021, "General");

        assertNotEquals(first, second);
    }
}
