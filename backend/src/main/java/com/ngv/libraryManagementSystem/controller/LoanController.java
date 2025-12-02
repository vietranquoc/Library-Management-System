package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.LoanRequest;
import com.ngv.libraryManagementSystem.dto.request.ReturnBookRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.LoanResponse;
import com.ngv.libraryManagementSystem.service.loan.LoanService;
import com.ngv.libraryManagementSystem.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    @PostMapping("/borrow")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<LoanResponse>> borrowBook(@Valid @RequestBody LoanRequest request) {
        Long memberId = userService.getCurrentMemberId();
        LoanResponse loan = loanService.borrowBook(request, memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Mượn sách thành công", loan));
    }

    @PostMapping("/return")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<LoanResponse>> returnBook(@Valid @RequestBody ReturnBookRequest request) {
        Long memberId = userService.getCurrentMemberId();
        LoanResponse loan = loanService.returnBook(request, memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Trả sách thành công", loan));
    }

    @GetMapping("/my-loans")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getMyLoans() {
        Long memberId = userService.getCurrentMemberId();
        List<LoanResponse> loans = loanService.getMyLoans(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách mượn sách thành công", loans));
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getLoansByBookId(@PathVariable Long bookId) {
        List<LoanResponse> loans = loanService.getLoansByBookId(bookId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách mượn sách thành công", loans));
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getMemberLoans(@PathVariable Long memberId) {
        List<LoanResponse> loans = loanService.getMemberLoans(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách mượn sách thành công", loans));
    }
}

