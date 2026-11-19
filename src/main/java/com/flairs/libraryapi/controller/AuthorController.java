package com.flairs.libraryapi.controller;

import com.flairs.libraryapi.dto.AuthorResponse;
import com.flairs.libraryapi.dto.BookResponse;
import com.flairs.libraryapi.dto.CreateAuthorRequest;
import com.flairs.libraryapi.service.AuthorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuthorResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}/books")
    public List<BookResponse> books(@PathVariable Long id) {
        return service.booksOf(id);
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody CreateAuthorRequest request) {
        AuthorResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/authors/" + saved.id())).body(saved);
    }
}
