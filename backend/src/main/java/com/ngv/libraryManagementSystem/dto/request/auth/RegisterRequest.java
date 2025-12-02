package com.ngv.libraryManagementSystem.dto.request.auth;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
