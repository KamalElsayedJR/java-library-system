
package com.flairs.libraryapi.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import com.flairs.library_api.exception.BookNotFoundException;
import com.flairs.library_api.exception.DuplicateIsbnException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// *TODO 4: turn exceptions into ProblemDetail answers.*
//   *BookNotFoundException   -> 404, title "Book not found"*
//   *DuplicateIsbnException  -> 409, title "Duplicate ISBN"*
//   *Validation failure      -> 400 with an "errors" map { field: message }*
//     *(override handleMethodArgumentNotValid, copy the pattern from the Task API)*
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBookNotFound(
            BookNotFoundException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setTitle("Book not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateIsbn(
            DuplicateIsbnException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problem.setTitle("Duplicate ISBN");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            org.springframework.http.HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );

        problem.setTitle("Validation failure");
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }
}
