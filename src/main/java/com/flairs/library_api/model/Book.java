package com.flairs.library_api.model;

public record Book(Long id, String title, String author, String isbn, int year, int copies) {

    public Book withId(Long newId) {
        return new Book(newId, title, author, isbn, year, copies);
    }
}
