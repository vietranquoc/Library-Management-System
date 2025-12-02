package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.LoanEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    
    List<LoanEntity> findByMember(MemberEntity member);
    
    List<LoanEntity> findByBookCopy(BookCopyEntity bookCopy);
    
    @Query("SELECT l FROM LoanEntity l WHERE l.bookCopy.book.id = :bookId")
    List<LoanEntity> findByBookId(@Param("bookId") Long bookId);
    
    @Query("SELECT l FROM LoanEntity l WHERE l.member.id = :memberId AND l.returnedDate IS NULL")
    List<LoanEntity> findActiveLoansByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT l FROM LoanEntity l WHERE l.returnedDate IS NULL AND l.dueDate < :currentDate")
    List<LoanEntity> findOverdueLoans(@Param("currentDate") LocalDate currentDate);
    
    Optional<LoanEntity> findByBookCopyAndReturnedDateIsNull(BookCopyEntity bookCopy);
}

