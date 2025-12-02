package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineResponse {
    private Long id;
    private BigDecimal amount;
    private Boolean paid;
    private LocalDate issueDate;
    private MemberInfo member;
    private LoanInfo loan;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanInfo {
        private Long id;
        private LocalDate loanDate;
        private LocalDate dueDate;
        private String bookTitle;
    }
}

