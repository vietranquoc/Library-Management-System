package com.ngv.libraryManagementSystem.service.auth;

import com.ngv.libraryManagementSystem.dto.request.auth.LoginRequest;
import com.ngv.libraryManagementSystem.dto.request.auth.RegisterRequest;
import com.ngv.libraryManagementSystem.dto.response.auth.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
