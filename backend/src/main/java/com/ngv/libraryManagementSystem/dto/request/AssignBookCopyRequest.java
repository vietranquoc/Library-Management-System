package com.ngv.libraryManagementSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignBookCopyRequest {
    @NotBlank(message = "Bar code is required")
    private String barCode;
}

