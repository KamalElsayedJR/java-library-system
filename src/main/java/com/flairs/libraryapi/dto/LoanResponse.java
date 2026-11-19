package com.flairs.libraryapi.dto;

import java.time.Instant;
import java.time.LocalDate;

public record LoanResponse(Long id, Long bookId, String bookTitle, Long memberId, String memberName,
                           Instant borrowedAt, LocalDate dueDate, Instant returnedAt) {
}
