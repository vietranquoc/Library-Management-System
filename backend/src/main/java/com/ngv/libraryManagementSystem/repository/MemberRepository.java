package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
}
