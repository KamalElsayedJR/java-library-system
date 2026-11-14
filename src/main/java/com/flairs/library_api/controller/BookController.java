
package com.flairs.library_api.controller;

import com.flairs.library_api.dto.CreateBookRequest;
import com.flairs.library_api.dto.BookResponse;
import com.flairs.library_api.model.Book;
import com.flairs.library_api.services.BookServices;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// *TODO 3: build the controller under /api/books. Keep it thin: map the DTO, call the service, return the result.*
//   *GET    /api/books?author=     -> List<BookResponse>*
//   *GET    /api/books/{id}        -> BookResponse*
//   *POST   /api/books             -> 201 + Location, body validated with @Valid*
//   *PUT    /api/books/{id}        -> BookResponse, body validated with @Valid*
//   *DELETE /api/books/{id}        -> 204*
// Write two private helpers: Book toBook(CreateBookRequest) and BookResponse toResponse(Book).
// Inject BookService through the constructor.
@Tag(name = "Books", description = "Books management APIs")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookServices service;

    public BookController(BookServices service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> findAll(
            @RequestParam(required = false) String author) {

        return service.findAll(author)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {

        Book book = service.findById(id);

        return toResponse(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody CreateBookRequest request) {

        Book createdBook = service.create(toBook(request));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBook.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(toResponse(createdBook));
    }

    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateBookRequest request) {

        Book updatedBook = service.update(id, toBook(request));

        return toResponse(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    private Book toBook(CreateBookRequest request) {

        return new Book(
                null,
                request.title(),
                request.author(),
                request.isbn(),
                request.year(),
                request.copies()
        );
    }

    private BookResponse toResponse(Book book) {

        return new BookResponse(
                book.id(),
                book.title(),
                book.author(),
                book.isbn(),
                book.year(),
                book.copies()
        );
    }
}
