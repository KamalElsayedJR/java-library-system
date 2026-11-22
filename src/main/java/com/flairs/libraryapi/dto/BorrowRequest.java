package com.flairs.libraryapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Session 12: memberId is optional. A MEMBER always borrows for themselves (the id comes from the token).
// A LIBRARIAN must say for whom.
public record BorrowRequest(@NotNull @Positive Long bookId, @Positive Long memberId) {
}
