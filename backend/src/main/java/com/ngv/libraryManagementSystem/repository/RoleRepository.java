package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByName(String name);
}
