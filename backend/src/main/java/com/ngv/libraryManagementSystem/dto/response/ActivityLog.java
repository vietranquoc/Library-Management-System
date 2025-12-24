package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    private String type; // "LOAN_REQUEST", "LOAN_BORROWED", "LOAN_RETURNED", "BOOK_ADDED", etc.
    private String description;
    private String memberName; // null nếu không phải member activity
    private String bookTitle;
    private LocalDateTime timestamp;
}

