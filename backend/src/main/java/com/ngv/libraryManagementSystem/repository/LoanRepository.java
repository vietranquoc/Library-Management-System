package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.LoanEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.enums.LoanStatusEnum;
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

    @Query("SELECT l FROM LoanEntity l WHERE l.status = :status AND l.returnedDate IS NULL AND l.dueDate < :currentDate")
    List<LoanEntity> findOverdueLoansByStatus(@Param("status") LoanStatusEnum status, @Param("currentDate") LocalDate currentDate);

    long countByBookIdAndReturnedDateIsNull(Long bookId);

    @Query("SELECT COUNT(l) > 0 FROM LoanEntity l WHERE l.member.id = :memberId AND l.book.id = :bookId AND l.returnedDate IS NULL")
    boolean existsActiveLoanByMemberAndBook(@Param("memberId") Long memberId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(l) > 0 FROM LoanEntity l WHERE l.bookCopy.id = :bookCopyId AND l.returnedDate IS NULL")
    boolean existsActiveLoanByBookCopy(@Param("bookCopyId") Long bookCopyId);

    // Statistics queries
    @Query("SELECT COUNT(l) FROM LoanEntity l WHERE l.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<LoanStatusEnum> statuses);

    @Query("SELECT COUNT(l) FROM LoanEntity l WHERE l.status = :status")
    long countOverdueBooks(@Param("status") LoanStatusEnum status);

    @Query("SELECT COUNT(l) FROM LoanEntity l WHERE l.status = :status AND l.loanDate IS NOT NULL AND YEAR(l.loanDate) = :year AND MONTH(l.loanDate) = :month")
    long countBorrowedInMonth(@Param("status") LoanStatusEnum status, @Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(l) FROM LoanEntity l WHERE l.status = :status AND l.returnedDate IS NOT NULL AND YEAR(l.returnedDate) = :year AND MONTH(l.returnedDate) = :month")
    long countReturnedInMonth(@Param("status") LoanStatusEnum status, @Param("year") int year, @Param("month") int month);

    @Query("SELECT l FROM LoanEntity l ORDER BY l.id DESC")
    List<LoanEntity> findAllOrderByIdDesc();

    @Query("SELECT l FROM LoanEntity l WHERE l.status = :status ORDER BY l.id DESC")
    List<LoanEntity> findByStatusOrderByIdDesc(@Param("status") LoanStatusEnum status);
}

