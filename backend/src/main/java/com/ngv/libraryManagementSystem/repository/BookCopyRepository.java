package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.BookCopyEntity;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.enums.BookCopyStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopyEntity, Long> {

    List<BookCopyEntity> findByBook(BookEntity book);

    @Query("SELECT bc FROM BookCopyEntity bc WHERE bc.book.id = :bookId AND bc.status = :status")
    List<BookCopyEntity> findByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") BookCopyStatusEnum status);

    @Query("SELECT bc FROM BookCopyEntity bc WHERE bc.book.id = :bookId AND bc.status = 'AVAILABLE'")
    List<BookCopyEntity> findAvailableCopiesByBookId(@Param("bookId") Long bookId);

    Optional<BookCopyEntity> findByBarCode(String barCode);

    long countByBookIdAndStatus(Long bookId, BookCopyStatusEnum status);
}

