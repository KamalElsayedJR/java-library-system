package com.flairs.libraryapi.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.flairs.libraryapi.TestcontainersConfiguration;
import com.flairs.libraryapi.model.Book;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// Data slice on a real PostgreSQL. Flyway runs V1..V6, so the seed is there:
// 6 books, 2 by George Orwell; "Animal Farm" (book 2) is on loan to Sara (member 1).
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class BookRepositoryTest {

    @Autowired BookRepository books;
    @Autowired AuthorRepository authors;
    @Autowired LoanRepository loans;

    // TODO 6
    @Test
    void searchByAuthorIgnoresCase() {
        var result = books.search("ORWELL", PageRequest.of(0, 10, Sort.by("year")));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(Book::getTitle)
            .containsExactly("Animal Farm", "1984");
    }

    @Test
    void noFilterReturnsEveryBook() {
        assertThat(books.search(null, PageRequest.of(0, 10)).getTotalElements()).isEqualTo(6);
    }

    // TODO 7
    @Test
    void theDatabaseRejectsADuplicateIsbn() {
        var author = authors.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Author 1 not found"));
        assertThatThrownBy(() -> books.saveAndFlush(new Book("Test Book", "9780451524935", 2020, 1, author)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void activeLoansAreCounted() {
        assertThat(loans.countByBookIdAndReturnedAtIsNull(2L)).isEqualTo(1);
        assertThat(loans.countByMemberIdAndReturnedAtIsNull(1L)).isEqualTo(1);
        assertThat(loans.countByMemberIdAndReturnedAtIsNull(2L)).isZero();
    }
}
