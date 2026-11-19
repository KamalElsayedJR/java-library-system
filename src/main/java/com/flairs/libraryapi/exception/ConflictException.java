package com.flairs.libraryapi.exception;

// A business rule says no: duplicate ISBN, no copies left, too many loans, already returned
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
