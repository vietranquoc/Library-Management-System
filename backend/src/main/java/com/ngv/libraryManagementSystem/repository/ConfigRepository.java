package com.ngv.libraryManagementSystem.repository;

import com.ngv.libraryManagementSystem.entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {

    Optional<ConfigEntity> findByConfigKey(String configKey);

    boolean existsByConfigKey(String configKey);

    List<ConfigEntity> findByConfigGroup(String configGroup);

    List<ConfigEntity> findAllByOrderByConfigGroupAscConfigKeyAsc();
}

