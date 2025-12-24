package com.ngv.libraryManagementSystem.entity;

import com.ngv.libraryManagementSystem.enums.LoanStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate loanDate;

    private LocalDate dueDate;

    private LocalDate returnedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatusEnum status = LoanStatusEnum.REQUESTED;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @ManyToOne
    @JoinColumn(name = "book_copy_id")
    private BookCopyEntity bookCopy;
}
