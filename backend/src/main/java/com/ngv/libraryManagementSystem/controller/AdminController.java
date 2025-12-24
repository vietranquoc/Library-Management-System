package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.*;
import com.ngv.libraryManagementSystem.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<Void>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        adminService.createCategory(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm thể loại thành công", null));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getAllCategories() {
        List<CategorySimpleResponse> categories = adminService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thể loại thành công", categories));
    }

    /**
     * Thêm mới sách (chỉ thông tin sách, không tạo bản sao/copy).
     * ADMIN hoặc STAFF đều có thể thao tác.
     */
    @PostMapping("/books")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody CreateBookRequest request) {
        BookResponse book = adminService.createBook(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm sách thành công", book));
    }

    @GetMapping("/authors")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<AuthorSimpleResponse>>> getAllAuthors() {
        List<AuthorSimpleResponse> authors = adminService.getAllAuthors();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách tác giả thành công", authors));
    }

    /**
     * Thêm mới tài khoản nhân viên (staff)
     * Tự động gán ROLE_STAFF cho user.
     * Chỉ ADMIN được phép thao tác.
     */
    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        Long staffId = adminService.createStaff(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thêm nhân viên thành công", staffId));
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StaffSimpleResponse>>> getAllStaff() {
        List<StaffSimpleResponse> staffs = adminService.getAllStaffs();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách nhân viên thành công", staffs));
    }

    @GetMapping("/dashboard/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<DashboardStatisticsResponse>> getDashboardStatistics() {
        DashboardStatisticsResponse statistics = adminService.getDashboardStatistics();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thống kê dashboard thành công", statistics));
    }
}


