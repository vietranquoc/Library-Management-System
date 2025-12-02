package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.PayFineRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.FineResponse;
import com.ngv.libraryManagementSystem.service.fine.FineService;
import com.ngv.libraryManagementSystem.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;
    private final UserService userService;

    @GetMapping("/my-fines")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getMyFines() {
        Long memberId = userService.getCurrentMemberId();
        List<FineResponse> fines = fineService.getMyFines(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách phạt thành công", fines));
    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<FineResponse>> payFine(@Valid @RequestBody PayFineRequest request) {
        Long memberId = userService.getCurrentMemberId();
        FineResponse fine = fineService.payFine(request, memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Thanh toán phạt thành công", fine));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getAllFines() {
        List<FineResponse> fines = fineService.getAllFines();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách phạt thành công", fines));
    }

    @PostMapping("/process-overdue")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> processOverdueLoans() {
        fineService.processOverdueLoans();
        return ResponseEntity.ok(new ApiResponse<>(200, "Xử lý sách quá hạn thành công", null));
    }
}

