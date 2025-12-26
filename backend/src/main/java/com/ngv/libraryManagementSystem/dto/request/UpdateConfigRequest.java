package com.ngv.libraryManagementSystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigRequest {

    @NotNull(message = "Số ngày mượn sách không được để trống")
    @Min(value = 1, message = "Số ngày mượn sách phải lớn hơn 0")
    private Integer loanPeriodDays;

    @NotNull(message = "Tiền phạt mỗi ngày không được để trống")
    @Min(value = 0, message = "Tiền phạt mỗi ngày phải lớn hơn hoặc bằng 0")
    private BigDecimal finePerDay;

    @NotNull(message = "Số sách tối đa mỗi member không được để trống")
    @Min(value = 1, message = "Số sách tối đa mỗi member phải lớn hơn 0")
    private Integer maxBooksPerMember;
}

