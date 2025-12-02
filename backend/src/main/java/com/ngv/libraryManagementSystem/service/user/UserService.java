package com.ngv.libraryManagementSystem.service.user;

import com.ngv.libraryManagementSystem.entity.UserEntity;

public interface UserService {
    UserEntity getCurrentUser();
    Long getCurrentMemberId();
}

