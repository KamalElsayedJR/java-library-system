package com.flairs.libraryapi.repository;

import com.flairs.libraryapi.model.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByBookIdAndReturnedAtIsNull(Long bookId);

    long countByMemberIdAndReturnedAtIsNull(Long memberId);

    @Query("""
            select l from Loan l join fetch l.book b join fetch b.author join fetch l.member m
            where m.id = :memberId order by l.borrowedAt desc
            """)
    List<Loan> findByMemberIdWithDetails(@Param("memberId") Long memberId);
}
