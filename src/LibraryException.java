class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String message) {
        super(message);
    }
}

class MemberNotFoundException extends LibraryException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}

class BookUnavailableException extends LibraryException {
    public BookUnavailableException(String message) {
        super(message);
    }
}

class LoanLimitException extends LibraryException {
    public LoanLimitException(String message) {
        super(message);
    }
}

class DuplicateBookException extends LibraryException {
    public DuplicateBookException(String message) {
        super(message);
    }
}
