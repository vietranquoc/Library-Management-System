package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.UserEntity;
import com.ngv.libraryManagementSystem.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    List<UserEntity> findByRolesContaining(RoleEntity role);
}
