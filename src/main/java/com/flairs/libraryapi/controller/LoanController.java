package com.flairs.libraryapi.controller;

import com.flairs.libraryapi.dto.BorrowRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.security.CurrentMember;
import com.flairs.libraryapi.service.LoanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@Valid @RequestBody BorrowRequest request, @AuthenticationPrincipal Jwt jwt) {
        LoanResponse loan = service.borrow(request, CurrentMember.from(jwt));
        return ResponseEntity.created(URI.create("/api/loans/" + loan.id())).body(loan);
    }

    @PatchMapping("/{id}/return")
    public LoanResponse giveBack(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.giveBack(id, CurrentMember.from(jwt));
    }
}
