package com.github.adetiamarhadi.xyz_auth_service.repository;

import com.github.adetiamarhadi.xyz_auth_service.entity.UserOTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOTPRepository extends JpaRepository<UserOTPEntity, Long> {
}
