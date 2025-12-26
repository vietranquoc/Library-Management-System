package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.UpdateConfigRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
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
     * Lấy cấu hình hệ thống hiện tại
     * ADMIN và STAFF đều có thể xem
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<ConfigResponse>> getConfig() {
        ConfigResponse config = configService.getConfig();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy cấu hình hệ thống thành công", config));
    }

    /**
     * Cập nhật cấu hình hệ thống
     * Chỉ ADMIN được phép cập nhật
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ConfigResponse>> updateConfig(@Valid @RequestBody UpdateConfigRequest request) {
        ConfigResponse config = configService.updateConfig(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật cấu hình hệ thống thành công", config));
    }
}

