package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyLoanData {
    private String month; // Format: "YYYY-MM" hoặc "Tháng MM/YYYY"
    private Long borrowedCount;
    private Long returnedCount;
}

