package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.auth.LoginRequest;
import com.ngv.libraryManagementSystem.dto.request.auth.RegisterRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.auth.AuthResponse;
import com.ngv.libraryManagementSystem.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse res = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng ký thành công", res));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse res = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng nhập thành công", res));
    }
}
