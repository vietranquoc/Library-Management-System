package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.CreateConfigRequest;
import com.ngv.libraryManagementSystem.dto.request.UpdateConfigRequest;
import com.ngv.libraryManagementSystem.dto.request.UpdateConfigValueRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigItemResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigListResponse;
import com.ngv.libraryManagementSystem.dto.response.ConfigResponse;
import com.ngv.libraryManagementSystem.service.config.ConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    /**
     * Lấy cấu hình hệ thống hiện tại (backward compatibility)
     * ADMIN và STAFF đều có thể xem
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConfigResponse>> getConfig() {
        ConfigResponse config = configService.getConfig();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy cấu hình hệ thống thành công", config));
    }

    /**
     * Cập nhật cấu hình hệ thống (backward compatibility)
     * Chỉ ADMIN được phép cập nhật
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ConfigResponse>> updateConfig(@Valid @RequestBody UpdateConfigRequest request) {
        ConfigResponse config = configService.updateConfig(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật cấu hình hệ thống thành công", config));
    }

    /**
     * Lấy tất cả configs (key-value format)
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConfigListResponse>> getAllConfigs() {
        ConfigListResponse configs = configService.getAllConfigs();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách cấu hình thành công", configs));
    }

    /**
     * Lấy config theo key
     */
    @GetMapping("/{configKey}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConfigItemResponse>> getConfigByKey(@PathVariable String configKey) {
        ConfigItemResponse config = configService.getConfigByKey(configKey);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy cấu hình thành công", config));
    }

    /**
     * Tạo config mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ConfigItemResponse>> createConfig(@Valid @RequestBody CreateConfigRequest request) {
        ConfigItemResponse config = configService.createConfig(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo cấu hình thành công", config));
    }

    /**
     * Cập nhật giá trị config theo key
     */
    @PutMapping("/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ConfigItemResponse>> updateConfigValue(
            @PathVariable String configKey,
            @Valid @RequestBody UpdateConfigValueRequest request) {
        ConfigItemResponse config = configService.updateConfigValue(configKey, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật cấu hình thành công", config));
    }

    /**
     * Xóa config
     */
    @DeleteMapping("/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteConfig(@PathVariable String configKey) {
        configService.deleteConfig(configKey);
        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa cấu hình thành công", null));
    }
}

