package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigResponse {
    private Long id;
    private Integer loanPeriodDays;
    private BigDecimal finePerDay;
    private Integer maxBooksPerMember;
}

