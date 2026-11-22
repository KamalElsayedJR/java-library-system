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

    // Paged search. The author is loaded in the same query (no N+1).
    // coalesce(:author, '') turns "no filter" into like '%%', which matches every row. Do not write
    // (:author is null or lower(...) like lower(:author)): a null String reaches PostgreSQL without
    // a type and lower(bytea) fails with a 500.
    @EntityGraph(attributePaths = "author")
    @Query("""
            select b from Book b
            where lower(b.author.name) like lower(concat('%', coalesce(:author, ''), '%'))
            """)
    Page<Book> search(@Param("author") String author, Pageable pageable);

    @Query("select b from Book b join fetch b.author a where a.id = :authorId order by b.year")
    List<Book> findByAuthorIdWithAuthor(@Param("authorId") Long authorId);
}
