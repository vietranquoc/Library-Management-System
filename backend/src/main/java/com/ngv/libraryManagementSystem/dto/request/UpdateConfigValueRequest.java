package com.ngv.libraryManagementSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigValueRequest {

    @NotBlank(message = "Config value không được để trống")
    private String configValue;
}

