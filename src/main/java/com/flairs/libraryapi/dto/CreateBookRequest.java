package com.flairs.libraryapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// Session 10 had "author" as text. Now a book points to an Author row by id.
public record CreateBookRequest(
        @NotBlank(message = "title is required") @Size(max = 120, message = "title must be at most 120 characters") String title,
        @NotBlank(message = "isbn is required") @Pattern(regexp = "\\d{13}", message = "isbn must be exactly 13 digits") String isbn,
        @Min(value = 1450, message = "year must be 1450 or later") @Max(value = 2100, message = "year must be 2100 or earlier") int year,
        @PositiveOrZero(message = "copies cannot be negative") int copies,
        @NotNull(message = "authorId is required") @Positive Long authorId) {
}
