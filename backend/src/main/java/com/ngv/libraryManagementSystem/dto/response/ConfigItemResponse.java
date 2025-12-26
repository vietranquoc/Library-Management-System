package com.ngv.libraryManagementSystem.dto.response;

import com.ngv.libraryManagementSystem.enums.ConfigDataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigItemResponse {
    private Long id;
    private String configKey;
    private String configValue;
    private ConfigDataTypeEnum dataType;
    private String description;
    private String configGroup;
}

