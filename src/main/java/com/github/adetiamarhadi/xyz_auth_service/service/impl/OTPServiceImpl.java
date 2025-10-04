package com.github.adetiamarhadi.xyz_auth_service.service.impl;

import com.github.adetiamarhadi.xyz_auth_service.entity.UserOTPEntity;
import com.github.adetiamarhadi.xyz_auth_service.otp.OTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserOTPRepository;
import com.github.adetiamarhadi.xyz_auth_service.service.OTPService;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transactional
    public boolean verify(String userUuid, OTPType otpType, String otpInput) {

        List<UserOTPEntity> entities = userOTPRepository
                .findAllByUserUuidAndOtpTypeAndUsedAtIsNull(userUuid, otpType.name());

        if (entities.isEmpty()) {
            throw new IllegalArgumentException("OTP not found");
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

                entity.setAttemptCount(entity.getAttemptCount() + 1);

                userOTPRepository.save(entity);
            }

            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        validEntity.setUsedAt(LocalDateTime.now());

        userOTPRepository.save(validEntity);

        userOTPRepository.deleteAllUnusedByUserUuidAndOtpType(userUuid, otpType.name());

        return true;
    }
}
