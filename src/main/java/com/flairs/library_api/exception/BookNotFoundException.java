package com.flairs.library_api.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book " + id + " not found");
    }
}
