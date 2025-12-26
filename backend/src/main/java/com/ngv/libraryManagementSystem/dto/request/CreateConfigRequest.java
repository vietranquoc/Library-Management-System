package com.ngv.libraryManagementSystem.dto.request;

import com.ngv.libraryManagementSystem.enums.ConfigDataTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConfigRequest {

    @NotBlank(message = "Config key không được để trống")
    private String configKey;

    @NotBlank(message = "Config value không được để trống")
    private String configValue;

    @NotNull(message = "Data type không được để trống")
    private ConfigDataTypeEnum dataType;

    private String description;

    private String configGroup;
}

