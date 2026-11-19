package com.flairs.libraryapi.dto;

public record BookResponse(Long id, String title, String isbn, int year, int copies, Long authorId, String authorName) {
}
