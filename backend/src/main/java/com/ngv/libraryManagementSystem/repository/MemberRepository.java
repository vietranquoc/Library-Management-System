package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    Optional<MemberEntity> findByEmail(String email);
}
