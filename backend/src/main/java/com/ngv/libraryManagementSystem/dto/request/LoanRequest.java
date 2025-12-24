package com.ngv.libraryManagementSystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;
}

