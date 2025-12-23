package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.LoanEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

    List<LoanEntity> findByMember(MemberEntity member);

    @Query("SELECT l FROM LoanEntity l WHERE l.book.id = :bookId")
    List<LoanEntity> findByBookId(@Param("bookId") Long bookId);

    @Query("SELECT l FROM LoanEntity l WHERE l.member.id = :memberId AND l.returnedDate IS NULL")
    List<LoanEntity> findActiveLoansByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT l FROM LoanEntity l WHERE l.returnedDate IS NULL AND l.dueDate < :currentDate")
    List<LoanEntity> findOverdueLoans(@Param("currentDate") LocalDate currentDate);

    long countByBookIdAndReturnedDateIsNull(Long bookId);
}

