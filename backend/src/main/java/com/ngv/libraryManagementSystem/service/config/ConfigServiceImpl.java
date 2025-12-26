package com.ngv.libraryManagementSystem.service.config;

import com.ngv.libraryManagementSystem.dto.request.CreateConfigRequest;
import com.ngv.libraryManagementSystem.dto.request.UpdateConfigRequest;
import com.ngv.libraryManagementSystem.dto.request.UpdateConfigValueRequest;
import com.ngv.libraryManagementSystem.dto.response.ConfigItemResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigListResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigResponse;
import com.ngv.libraryManagementSystem.entity.ConfigEntity;
import com.ngv.libraryManagementSystem.enums.ConfigDataTypeEnum;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.exception.NotFoundException;
import com.ngv.libraryManagementSystem.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public ConfigResponse getConfig() {
        Integer loanPeriodDays = getLoanPeriodDays();
        BigDecimal finePerDay = getFinePerDay();
        Integer maxBooksPerMember = getMaxBooksPerMember();
        
        return ConfigResponse.builder()
                .loanPeriodDays(loanPeriodDays)
                .finePerDay(finePerDay)
                .maxBooksPerMember(maxBooksPerMember)
                .build();
    }

    @Override
    @Transactional
    public ConfigResponse updateConfig(UpdateConfigRequest request) {
        // Update các config keys tương ứng
        setConfigValue(ConfigKeys.LOAN_PERIOD_DAYS, String.valueOf(request.getLoanPeriodDays()), ConfigDataTypeEnum.INTEGER);
        setConfigValue(ConfigKeys.FINE_PER_DAY, request.getFinePerDay().toString(), ConfigDataTypeEnum.DECIMAL);
        setConfigValue(ConfigKeys.MAX_BOOKS_PER_MEMBER, String.valueOf(request.getMaxBooksPerMember()), ConfigDataTypeEnum.INTEGER);
        
        log.info("Cập nhật cấu hình hệ thống thành công");
        return getConfig();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getLoanPeriodDays() {
        return getConfigValueAsInteger(ConfigKeys.LOAN_PERIOD_DAYS, 7);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getFinePerDay() {
        return getConfigValueAsDecimal(ConfigKeys.FINE_PER_DAY, new BigDecimal("10000"));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMaxBooksPerMember() {
        return getConfigValueAsInteger(ConfigKeys.MAX_BOOKS_PER_MEMBER, 5);
    }

    // ========== Key-Value Methods ==========

    /**
     * Lấy tất cả configs
     */
    @Transactional(readOnly = true)
    public ConfigListResponse getAllConfigs() {
        List<ConfigEntity> configs = configRepository.findAllByOrderByConfigGroupAscConfigKeyAsc();
        List<ConfigItemResponse> configItems = configs.stream()
                .map(this::mapToConfigItemResponse)
                .collect(Collectors.toList());
        
        return ConfigListResponse.builder()
                .loanPeriodDays(getLoanPeriodDays())
                .finePerDay(getFinePerDay())
                .maxBooksPerMember(getMaxBooksPerMember())
                .allConfigs(configItems)
                .build();
    }

    /**
     * Lấy config theo key
     */
    @Transactional(readOnly = true)
    public ConfigItemResponse getConfigByKey(String configKey) {
        ConfigEntity config = configRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy config với key: " + configKey));
        return mapToConfigItemResponse(config);
    }

    /**
     * Tạo config mới
     */
    @Transactional
    public ConfigItemResponse createConfig(CreateConfigRequest request) {
        if (configRepository.existsByConfigKey(request.getConfigKey())) {
            throw new BadRequestException("Config key đã tồn tại: " + request.getConfigKey());
        }
        
        ConfigEntity config = new ConfigEntity();
        config.setConfigKey(request.getConfigKey());
        config.setConfigValue(request.getConfigValue());
        config.setDataType(request.getDataType());
        config.setDescription(request.getDescription());
        config.setConfigGroup(request.getConfigGroup());
        
        ConfigEntity saved = configRepository.save(config);
        log.info("Tạo config mới: {}", request.getConfigKey());
        return mapToConfigItemResponse(saved);
    }

    /**
     * Cập nhật giá trị config theo key
     */
    @Transactional
    public ConfigItemResponse updateConfigValue(String configKey, UpdateConfigValueRequest request) {
        ConfigEntity config = configRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy config với key: " + configKey));
        
        config.setConfigValue(request.getConfigValue());
        ConfigEntity saved = configRepository.save(config);
        log.info("Cập nhật config: {}", configKey);
        return mapToConfigItemResponse(saved);
    }

    /**
     * Xóa config
     */
    @Transactional
    public void deleteConfig(String configKey) {
        ConfigEntity config = configRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy config với key: " + configKey));
        configRepository.delete(config);
        log.info("Xóa config: {}", configKey);
    }

    // ========== Helper Methods ==========

    /**
     * Lấy giá trị config theo key, nếu không có thì tạo với giá trị mặc định
     */
    private String getConfigValue(String key, String defaultValue, ConfigDataTypeEnum dataType, String description, String group) {
        return configRepository.findByConfigKey(key)
                .map(ConfigEntity::getConfigValue)
                .orElseGet(() -> {
                    log.info("Tạo config mặc định: {} = {}", key, defaultValue);
                    ConfigEntity config = new ConfigEntity();
                    config.setConfigKey(key);
                    config.setConfigValue(defaultValue);
                    config.setDataType(dataType);
                    config.setDescription(description);
                    config.setConfigGroup(group);
                    configRepository.save(config);
                    return defaultValue;
                });
    }

    /**
     * Set giá trị config, tạo mới nếu chưa có
     */
    private void setConfigValue(String key, String value, ConfigDataTypeEnum dataType) {
        ConfigEntity config = configRepository.findByConfigKey(key)
                .orElseGet(() -> {
                    ConfigEntity newConfig = new ConfigEntity();
                    newConfig.setConfigKey(key);
                    newConfig.setDataType(dataType);
                    newConfig.setConfigGroup(getGroupFromKey(key));
                    return newConfig;
                });
        config.setConfigValue(value);
        configRepository.save(config);
    }

    private Integer getConfigValueAsInteger(String key, Integer defaultValue) {
        String value = getConfigValue(key, String.valueOf(defaultValue), ConfigDataTypeEnum.INTEGER, 
                getDescriptionFromKey(key), getGroupFromKey(key));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Không thể parse config {} thành Integer, sử dụng giá trị mặc định: {}", key, defaultValue);
            return defaultValue;
        }
    }

    private BigDecimal getConfigValueAsDecimal(String key, BigDecimal defaultValue) {
        String value = getConfigValue(key, defaultValue.toString(), ConfigDataTypeEnum.DECIMAL,
                getDescriptionFromKey(key), getGroupFromKey(key));
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("Không thể parse config {} thành Decimal, sử dụng giá trị mặc định: {}", key, defaultValue);
            return defaultValue;
        }
    }

    private String getGroupFromKey(String key) {
        if (key.startsWith("loan.")) return "loan";
        if (key.startsWith("fine.")) return "fine";
        if (key.startsWith("member.")) return "member";
        return "general";
    }

    private String getDescriptionFromKey(String key) {
        switch (key) {
            case ConfigKeys.LOAN_PERIOD_DAYS:
                return "Số ngày mượn sách mặc định";
            case ConfigKeys.FINE_PER_DAY:
                return "Tiền phạt mỗi ngày quá hạn (VND)";
            case ConfigKeys.MAX_BOOKS_PER_MEMBER:
                return "Số sách tối đa mà một member có thể mượn cùng lúc";
            default:
                return "";
        }
    }

    private ConfigItemResponse mapToConfigItemResponse(ConfigEntity config) {
        return ConfigItemResponse.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .dataType(config.getDataType())
                .description(config.getDescription())
                .configGroup(config.getConfigGroup())
                .build();
    }
}
