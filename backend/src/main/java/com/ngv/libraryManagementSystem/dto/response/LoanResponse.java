package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private Long id;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private String status;
    private BookInfo book;
    private MemberInfo member;
    private String bookCopyBarCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {
        private Long id;
        private String title;
        private String isbn;
    }

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
}

