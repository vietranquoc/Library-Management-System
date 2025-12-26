package com.ngv.libraryManagementSystem.service.config;

import com.ngv.libraryManagementSystem.dto.request.UpdateConfigRequest;
import com.ngv.libraryManagementSystem.dto.response.ConfigResponse;
import com.ngv.libraryManagementSystem.entity.ConfigEntity;
import com.ngv.libraryManagementSystem.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public ConfigResponse getConfig() {
        ConfigEntity config = getOrCreateDefaultConfig();
        return mapToConfigResponse(config);
    }

    @Override
    @Transactional
    public ConfigResponse updateConfig(UpdateConfigRequest request) {
        ConfigEntity config = getOrCreateDefaultConfig();
        
        config.setLoanPeriodDays(request.getLoanPeriodDays());
        config.setFinePerDay(request.getFinePerDay());
        config.setMaxBooksPerMember(request.getMaxBooksPerMember());
        
        ConfigEntity savedConfig = configRepository.save(config);
        log.info("Cập nhật cấu hình hệ thống thành công");
        return mapToConfigResponse(savedConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getLoanPeriodDays() {
        ConfigEntity config = getOrCreateDefaultConfig();
        return config.getLoanPeriodDays();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getFinePerDay() {
        ConfigEntity config = getOrCreateDefaultConfig();
        return config.getFinePerDay();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMaxBooksPerMember() {
        ConfigEntity config = getOrCreateDefaultConfig();
        return config.getMaxBooksPerMember();
    }

    /**
     * Lấy config từ database, nếu chưa có thì tạo config mặc định
     */
    private ConfigEntity getOrCreateDefaultConfig() {
        return configRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    log.info("Không tìm thấy config, tạo config mặc định");
                    ConfigEntity defaultConfig = new ConfigEntity();
                    defaultConfig.setLoanPeriodDays(7);
                    defaultConfig.setFinePerDay(new BigDecimal("10000"));
                    defaultConfig.setMaxBooksPerMember(5);
                    return configRepository.save(defaultConfig);
                });
    }

    private ConfigResponse mapToConfigResponse(ConfigEntity config) {
        return ConfigResponse.builder()
                .id(config.getId())
                .loanPeriodDays(config.getLoanPeriodDays())
                .finePerDay(config.getFinePerDay())
                .maxBooksPerMember(config.getMaxBooksPerMember())
                .build();
    }
}

