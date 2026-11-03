import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        LibraryException exception =
                new LibraryException("Book not found");

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        LibraryException exception =
                new LibraryException("Error");

        assertInstanceOf(RuntimeException.class, exception);
    }
}
