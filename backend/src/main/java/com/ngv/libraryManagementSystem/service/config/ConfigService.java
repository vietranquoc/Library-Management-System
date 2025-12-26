package com.ngv.libraryManagementSystem.service.config;

import com.ngv.libraryManagementSystem.dto.request.UpdateConfigRequest;
import com.ngv.libraryManagementSystem.dto.response.ConfigResponse;

public interface ConfigService {
    ConfigResponse getConfig();
    ConfigResponse updateConfig(UpdateConfigRequest request);
    Integer getLoanPeriodDays();
    java.math.BigDecimal getFinePerDay();
    Integer getMaxBooksPerMember();
}

