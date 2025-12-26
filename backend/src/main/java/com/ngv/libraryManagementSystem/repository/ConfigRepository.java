package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {
    /**
     * Lấy config đầu tiên (chỉ có 1 config trong hệ thống)
     */
    Optional<ConfigEntity> findFirstByOrderByIdAsc();
}

