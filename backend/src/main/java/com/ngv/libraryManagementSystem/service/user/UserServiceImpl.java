package com.ngv.libraryManagementSystem.service.user;

import com.ngv.libraryManagementSystem.entity.UserEntity;
import com.ngv.libraryManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Long getCurrentMemberId() {
        UserEntity user = getCurrentUser();
        if (user.getMember() == null) {
            throw new RuntimeException("User is not associated with a member");
        }
        return user.getMember().getId();
    }
}

