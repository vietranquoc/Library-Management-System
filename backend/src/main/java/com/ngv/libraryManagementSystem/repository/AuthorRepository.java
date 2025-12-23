package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    Optional<AuthorEntity> findByNameIgnoreCase(String name);
}

