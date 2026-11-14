
package com.flairs.library_api.services;

import com.flairs.library_api.exception.BookNotFoundException;
import com.flairs.library_api.exception.DuplicateIsbnException;
import com.flairs.library_api.model.Book;
import com.flairs.library_api.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// *TODO 2: implement the service. It holds the business rules and knows nothing about HTTP.*
@Service
public class BookServices {

    private final BookRepository repository;

    public BookServices(BookRepository repository) {
        this.repository = repository;
        repository.save(new Book(null, "1984", "George Orwell", "9780451524935", 1949, 3));
        repository.save(new Book(null, "Clean Code", "Robert C. Martin", "9780132350884", 2008, 2));
    }

    // all books; if author is not null keep only books whose author contains it (ignore case)
    public List<Book> findAll(String author) {
        List<Book> books = repository.findAll();

        if (author == null || author.isBlank()) {
            return books;
        }

        return books.stream()
                .filter(book -> book.author() != null
                        && book.author().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    // throw BookNotFoundException when the id does not exist
    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // throw DuplicateIsbnException when the ISBN is already used
    public Book create(Book book) {
        boolean isbnExists = repository.findAll().stream()
                .anyMatch(existingBook -> existingBook.isbn().equals(book.isbn()));

        if (isbnExists) {
            throw new DuplicateIsbnException(book.isbn());
        }

        return repository.save(book);
    }

    // 404 if the id does not exist. 409 if the new ISBN belongs to ANOTHER book. Otherwise save with the same id.
    public Book update(Long id, Book book) {
        Book existingBook = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        boolean isbnExists = repository.findAll().stream()
                .anyMatch(existing -> existing.isbn().equals(book.isbn())
                        && !existing.id().equals(id));

        if (isbnExists) {
            throw new DuplicateIsbnException(book.isbn());
        }

        Book updatedBook = new Book(
                id,
                book.title(),
                book.author(),
                book.isbn(),
                book.year(),
                book.copies()
        );

        return repository.save(updatedBook);
    }

    // throw BookNotFoundException when nothing was deleted
    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            throw new BookNotFoundException(id);
        }
    }
}
