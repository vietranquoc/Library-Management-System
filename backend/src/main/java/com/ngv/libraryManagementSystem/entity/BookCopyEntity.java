package com.ngv.libraryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "book_copies")
public class BookCopyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String barCode;

    private boolean available = true;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @OneToMany(mappedBy = "bookCopy")
    private List<LoanEntity> loans;

    @OneToMany(mappedBy = "bookCopy")
    private List<ReservationEntity> reservations;
}
