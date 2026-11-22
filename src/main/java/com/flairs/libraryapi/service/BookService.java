package com.flairs.libraryapi.service;

import com.flairs.libraryapi.dto.BookResponse;
import com.flairs.libraryapi.dto.CreateBookRequest;
import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.dto.PageResponse;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Author;
import com.flairs.libraryapi.model.Book;
import com.flairs.libraryapi.repository.AuthorRepository;
import com.flairs.libraryapi.repository.BookRepository;
import com.flairs.libraryapi.repository.LoanRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository books;
    private final AuthorRepository authors;
    private final LoanRepository loans;

    public BookService(BookRepository books, AuthorRepository authors, LoanRepository loans) {
        this.books = books;
        this.authors = authors;
        this.loans = loans;
    }

    public PageResponse<BookResponse> search(String author, Pageable pageable) {
        return PageResponse.of(books.search(author, pageable), Mapper::toResponse);
    }

    public BookResponse findById(Long id) {
        return Mapper.toResponse(load(id));
    }

    @Transactional
    public BookResponse create(CreateBookRequest request) {
        if (books.existsByIsbn(request.isbn())) {
            throw new ConflictException("A book with ISBN " + request.isbn() + " already exists");
        }
        Author author = authors.findById(request.authorId())
                .orElseThrow(() -> new NotFoundException("Author", request.authorId()));
        Book book = new Book(request.title().trim(), request.isbn(), request.year(), request.copies(), author);
        return Mapper.toResponse(books.save(book));
    }

    @Transactional
    public BookResponse update(Long id, CreateBookRequest request) {
        Book book = load(id);
        if (books.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new ConflictException("A book with ISBN " + request.isbn() + " already exists");
        }
        Author author = authors.findById(request.authorId())
                .orElseThrow(() -> new NotFoundException("Author", request.authorId()));
        // No save() call: the entity is managed, so dirty checking writes the UPDATE at commit
        book.setTitle(request.title().trim());
        book.setIsbn(request.isbn());
        book.setYear(request.year());
        book.setCopies(request.copies());
        book.setAuthor(author);
        return Mapper.toResponse(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = load(id);
        if (loans.countByBookIdAndReturnedAtIsNull(id) > 0) {
            throw new ConflictException("Book " + id + " is on loan and cannot be deleted");
        }
        books.delete(book);
    }

    private Book load(Long id) {
        return books.findById(id).orElseThrow(() -> new NotFoundException("Book", id));
    }
}
