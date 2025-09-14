package com.github.adetiamarhadi.xyz_auth_service.repository;

import com.github.adetiamarhadi.xyz_auth_service.entity.UserOTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOTPRepository extends JpaRepository<UserOTPEntity, Long> {

    List<UserOTPEntity> findAllByUserUuidAndOtpTypeAndUsedAtIsNull(String userUuid, String otpType);

    @Modifying
    @Query("DELETE FROM UserOTPEntity u WHERE u.userUuid = :userUuid AND u.otpType = :otpType AND u.usedAt IS NULL")
    void deleteAllUnusedByUserUuidAndOtpType(String userUuid, String otpType);
}
