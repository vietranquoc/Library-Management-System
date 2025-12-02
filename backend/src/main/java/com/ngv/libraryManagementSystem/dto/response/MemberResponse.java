package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate joinDate;
    private String status;
    private AddressInfo address;
    private List<LoanInfo> loans;
    private List<ReservationInfo> reservations;
    private List<FineInfo> fines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressInfo {
        private Long id;
        private String street;
        private String city;
        private String state;
        private String zipCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanInfo {
        private Long id;
        private LocalDate loanDate;
        private LocalDate dueDate;
        private LocalDate returnedDate;
        private String bookTitle;
        private String bookCopyBarCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationInfo {
        private Long id;
        private LocalDate reservationDate;
        private Boolean notified;
        private String bookTitle;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FineInfo {
        private Long id;
        private java.math.BigDecimal amount;
        private Boolean paid;
        private LocalDate issueDate;
    }
}

