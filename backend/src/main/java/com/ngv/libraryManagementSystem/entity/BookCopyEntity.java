package com.ngv.libraryManagementSystem.entity;

import com.ngv.libraryManagementSystem.enums.BookCopyStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_copies")
public class BookCopyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String barCode; // Mã vạch của bản sao

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookCopyStatusEnum status = BookCopyStatusEnum.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @OneToMany(mappedBy = "bookCopy")
    private List<LoanEntity> loans;
}

