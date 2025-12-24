package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponse {
    private Long totalBooks;
    private Long activeMembers;
    private Long totalBorrowedBooks;
    private Long overdueBooks;
    private List<MonthlyLoanData> monthlyLoanData;
    private List<CategoryDistribution> categoryDistribution;
    private List<ActivityLog> recentActivities;
}

