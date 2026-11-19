package com.flairs.libraryapi.service;

import com.flairs.libraryapi.dto.AuthorResponse;
import com.flairs.libraryapi.dto.BookResponse;
import com.flairs.libraryapi.dto.CreateAuthorRequest;
import com.flairs.libraryapi.dto.Mapper;
import com.flairs.libraryapi.exception.ConflictException;
import com.flairs.libraryapi.exception.NotFoundException;
import com.flairs.libraryapi.model.Author;
import com.flairs.libraryapi.repository.AuthorRepository;
import com.flairs.libraryapi.repository.BookRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authors;
    private final BookRepository books;

    public AuthorService(AuthorRepository authors, BookRepository books) {
        this.authors = authors;
        this.books = books;
    }

    public List<AuthorResponse> findAll() {
        return authors.findAll(Sort.by("name")).stream().map(Mapper::toResponse).toList();
    }

    public List<BookResponse> booksOf(Long authorId) {
        if (!authors.existsById(authorId)) {
            throw new NotFoundException("Author", authorId);
        }
        return books.findByAuthorIdWithAuthor(authorId).stream().map(Mapper::toResponse).toList();
    }

    @Transactional
    public AuthorResponse create(CreateAuthorRequest request) {
        String name = request.name().trim();
        if (authors.findByNameIgnoreCase(name).isPresent()) {
            throw new ConflictException("Author '" + name + "' already exists");
        }
        return Mapper.toResponse(authors.save(new Author(name)));
    }
}
