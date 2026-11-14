package com.flairs.library_api.dto;

public record BookResponse(Long id, String title, String author, String isbn, int year, int copies) {
}
