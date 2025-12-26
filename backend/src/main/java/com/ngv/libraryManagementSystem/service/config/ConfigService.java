package com.ngv.libraryManagementSystem.service.config;

import com.ngv.libraryManagementSystem.dto.request.CreateConfigRequest;
import com.ngv.libraryManagementSystem.dto.request.UpdateConfigRequest;
import com.ngv.libraryManagementSystem.dto.request.UpdateConfigValueRequest;
import com.ngv.libraryManagementSystem.dto.response.ConfigItemResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigListResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigResponse;

public interface ConfigService {
    // Backward compatibility methods
    ConfigResponse getConfig();
    ConfigResponse updateConfig(UpdateConfigRequest request);
    Integer getLoanPeriodDays();
    java.math.BigDecimal getFinePerDay();
    Integer getMaxBooksPerMember();
    
    // Key-value methods
    ConfigListResponse getAllConfigs();
    ConfigItemResponse getConfigByKey(String configKey);
    ConfigItemResponse createConfig(CreateConfigRequest request);
    ConfigItemResponse updateConfigValue(String configKey, UpdateConfigValueRequest request);
    void deleteConfig(String configKey);
}

