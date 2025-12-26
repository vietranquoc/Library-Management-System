package com.ngv.libraryManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response cho backward compatibility với frontend hiện tại
 * Vẫn trả về format cũ nhưng lấy từ key-value store
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigListResponse {
    private Integer loanPeriodDays;
    private BigDecimal finePerDay;
    private Integer maxBooksPerMember;
    private List<ConfigItemResponse> allConfigs;
}

