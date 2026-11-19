package com.flairs.libraryapi.repository;

import com.flairs.libraryapi.model.Book;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    // TODO 4: add a paged search:  Page<Book> search(String author, Pageable pageable)
    //  - optional filter: when author is null return all books, otherwise match the author
    //    name with a case-insensitive "contains"
    //  - load the author in the SAME query (@EntityGraph) so the list has no N+1
    //  - hint: for a String filter use coalesce(:author, '') inside the like. The pattern
    //    (:author is null or lower(...) like ...) fails on PostgreSQL with "lower(bytea) does not exist"
    // Then use it in BookService.search instead of findAll.
    @EntityGraph(attributePaths = "author")
    @Query("""
        SELECT b
        FROM Book b
        WHERE LOWER(b.author.name)
        LIKE LOWER(CONCAT('%', COALESCE(:author, ''), '%'))
    """)
    Page<Book> search(
            @Param("author") String author,
            Pageable pageable
    );
    @Query("select b from Book b join fetch b.author a where a.id = :authorId order by b.year")
    List<Book> findByAuthorIdWithAuthor(@Param("authorId") Long authorId);
}
