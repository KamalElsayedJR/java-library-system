package com.flairs.libraryapi.controller;

import com.flairs.libraryapi.dto.CreateMemberRequest;
import com.flairs.libraryapi.dto.LoanResponse;
import com.flairs.libraryapi.dto.MemberResponse;
import com.flairs.libraryapi.security.CurrentMember;
import com.flairs.libraryapi.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    // Role check on the method. The URL rules in SecurityConfig are the other way to do it.
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping
    public List<MemberResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.findById(id, CurrentMember.from(jwt));
    }

    @GetMapping("/{id}/loans")
    public List<LoanResponse> loans(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.loansOf(id, CurrentMember.from(jwt));
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/members/" + saved.id())).body(saved);
    }
}
