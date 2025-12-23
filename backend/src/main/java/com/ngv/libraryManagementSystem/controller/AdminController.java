package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.admin.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Thêm mới thể loại sách (Category)
     * Chỉ ADMIN được phép thao tác.
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        adminService.createCategory(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm thể loại thành công", null));
    }

    /**
     * Thêm mới sách (chỉ thông tin sách, không tạo bản sao/copy).
     * ADMIN hoặc STAFF đều có thể thao tác.
     */
    @PostMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Valid @RequestBody CreateBookRequest request
    ) {
        BookResponse book = adminService.createBook(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm sách thành công", book));
    }

    /**
     * Thêm mới tài khoản nhân viên (staff)
     * Tự động gán ROLE_STAFF cho user.
     * Chỉ ADMIN được phép thao tác.
     */
    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> createStaff(
            @Valid @RequestBody CreateStaffRequest request
    ) {
        Long staffId = adminService.createStaff(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm nhân viên thành công", staffId));
    }
}


