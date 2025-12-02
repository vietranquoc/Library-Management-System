package com.ngv.libraryManagementSystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayFineRequest {
    @NotNull(message = "Fine ID is required")
    private Long fineId;
}

