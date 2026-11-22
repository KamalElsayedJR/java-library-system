package com.flairs.libraryapi.dto;

import java.time.Instant;

// No passwordHash here: a DTO decides exactly what leaves the API
public record MemberResponse(Long id, String name, String email, String role, Instant joinedAt) {
}
