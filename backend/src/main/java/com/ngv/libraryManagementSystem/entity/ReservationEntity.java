package com.ngv.libraryManagementSystem.entity;

import com.ngv.libraryManagementSystem.enums.ReservationStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reservationDate;

    private Boolean notified = false; // đã gửi mail hay chưa

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatusEnum status = ReservationStatusEnum.PENDING;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;
}
