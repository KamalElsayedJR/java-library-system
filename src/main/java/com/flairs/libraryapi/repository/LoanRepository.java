package com.flairs.libraryapi.repository;

import com.flairs.libraryapi.model.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // TODO 3: these three methods have a "default" body that throws, so the starter compiles.
    // Delete each body (and the word default) so Spring Data writes the query for you.
    //
    // 3a: the method NAME is the query: count loans where book.id = ? and returnedAt is null.
    //     After you delete the body it is just:  long countByBookIdAndReturnedAtIsNull(Long bookId);
    long countByBookIdAndReturnedAtIsNull(Long bookId);
    // 3b: the same for one member
    long countByMemberIdAndReturnedAtIsNull(Long memberId);

    // 3c: a JPQL @Query that loads a member's loans WITH book, author and member in ONE select
    //     (join fetch), newest first. Use @Param("memberId").
    @Query("""
    SELECT l
    FROM Loan l
    JOIN FETCH l.book b
    JOIN FETCH b.author
    JOIN FETCH l.member
    WHERE l.member.id = :memberId
    ORDER BY l.borrowedAt DESC
""")
    List<Loan> findByMemberIdWithDetails(
            @Param("memberId") Long memberId
    );
}
