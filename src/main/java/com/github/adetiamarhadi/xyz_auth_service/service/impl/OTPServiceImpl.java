package com.github.adetiamarhadi.xyz_auth_service.service.impl;

import com.github.adetiamarhadi.xyz_auth_service.entity.UserOTPEntity;
import com.github.adetiamarhadi.xyz_auth_service.otp.OTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserOTPRepository;
import com.github.adetiamarhadi.xyz_auth_service.service.OTPService;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OTPServiceImpl implements OTPService {

    private final UserOTPRepository userOTPRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, OTPGenerator> otpGenerators;

    @Override
    public String generate(String userUuid, OTPType otpType) {

        OTPGenerator generator = otpGenerators.get("totpGenerator");

        String otp = generator.generate(userUuid, otpType);

        UserOTPEntity entity = UserOTPEntity.builder()
                .userUuid(userUuid)
                .otpType(otpType.name())
                .otpCodeHash(passwordEncoder.encode(otp))
                .expiredAt(LocalDateTime.now().plusMinutes(2))
                .attemptCount(0)
                .createdBy("system")
                .build();

        userOTPRepository.save(entity);

        return otp;
    }

    @Transactional
    @Override
    public boolean verify(String userUuid, OTPType otpType, String otpInput) {

        List<UserOTPEntity> entities = userOTPRepository
                .findAllByUserUuidAndOtpTypeAndUsedAtIsNull(userUuid, otpType.name());

        if (entities.isEmpty()) {

            log.warn("No OTP found for userUuid: {} and otpType: {}", userUuid, otpType);

            return false;
        }

        UserOTPEntity validEntity = null;
        for (UserOTPEntity entity : entities) {
            if (!entity.getExpiredAt().isBefore(LocalDateTime.now()) &&
                    passwordEncoder.matches(otpInput, entity.getOtpCodeHash())) {
                validEntity = entity;
                break;
            }
        }

        if (validEntity == null) {

            for (UserOTPEntity entity : entities) {

                int attemptCount = entity.getAttemptCount() + 1;

                entity.setAttemptCount(attemptCount);

                userOTPRepository.save(entity);

                if (attemptCount >= 3) {

                    userOTPRepository.deleteAllUnusedByUserUuidAndOtpType(userUuid, otpType.name());

                    log.warn("OTP entity deleted due to too many invalid attempts for userUuid: {} and otpType: {}",
                            userUuid, otpType);

                    throw new IllegalArgumentException("Too many invalid OTP attempts. Please request a new OTP.");
                }
            }

            log.warn("Invalid OTP attempt for userUuid: {} and otpType: {}", userUuid, otpType);

            return false;
        }

        validEntity.setAttemptCount(validEntity.getAttemptCount() + 1);
        validEntity.setUsedAt(LocalDateTime.now());

        userOTPRepository.save(validEntity);

        userOTPRepository.deleteAllUnusedByUserUuidAndOtpType(userUuid, otpType.name());

        return true;
    }
}
