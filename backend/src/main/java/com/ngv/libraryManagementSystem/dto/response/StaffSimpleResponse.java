package com.ngv.libraryManagementSystem.dto.response;

import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffSimpleResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private MemberStatusEnum status;
}


