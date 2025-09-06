package com.github.adetiamarhadi.xyz_auth_service.repository;

import com.github.adetiamarhadi.xyz_auth_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
