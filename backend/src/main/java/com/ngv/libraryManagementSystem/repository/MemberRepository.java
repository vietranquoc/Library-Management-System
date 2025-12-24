package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    Optional<MemberEntity> findByEmail(String email);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.status = :status")
    long countByStatus(@Param("status") MemberStatusEnum status);
}
