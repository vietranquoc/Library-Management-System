package com.ngv.libraryManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_config")
public class ConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Số ngày mượn sách mặc định (ví dụ: 7 ngày)
     */
    @Column(name = "loan_period_days", nullable = false)
    private Integer loanPeriodDays = 7;

    /**
     * Tiền phạt mỗi ngày quá hạn (VND)
     */
    @Column(name = "fine_per_day", nullable = false, precision = 19, scale = 2)
    private BigDecimal finePerDay = new BigDecimal("10000");

    /**
     * Số sách tối đa mà một member có thể mượn cùng lúc
     */
    @Column(name = "max_books_per_member", nullable = false)
    private Integer maxBooksPerMember = 5;
}

