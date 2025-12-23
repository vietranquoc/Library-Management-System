package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByMember(MemberEntity member);

    List<ReservationEntity> findByBook(BookEntity book);

    @Query("SELECT r FROM ReservationEntity r WHERE r.book.id = :bookId ORDER BY r.reservationDate ASC")
    List<ReservationEntity> findByBookIdOrderByDateAsc(@Param("bookId") Long bookId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.book.id = :bookId AND r.notified = false ORDER BY r.reservationDate ASC")
    List<ReservationEntity> findPendingReservationsByBookId(@Param("bookId") Long bookId);

    Optional<ReservationEntity> findByMemberAndBook(MemberEntity member, BookEntity book);
}

