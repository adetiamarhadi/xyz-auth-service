package com.github.adetiamarhadi.xyz_auth_service.service.impl;

import com.github.adetiamarhadi.xyz_auth_service.dto.ForgotPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.OtpVerificationRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.RefreshTokenRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResendOtpRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResetPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.SignupRequest;
import com.github.adetiamarhadi.xyz_auth_service.entity.UserEntity;
import com.github.adetiamarhadi.xyz_auth_service.notification.OTPNotificationService;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import com.github.adetiamarhadi.xyz_auth_service.service.AuthService;
import com.github.adetiamarhadi.xyz_auth_service.service.OTPService;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import com.github.adetiamarhadi.xyz_auth_service.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OTPService otpService;
    private final OTPNotificationService loggingOTPNotificationServiceImpl;

    @Transactional
    @Override
    public GenericResponse signup(SignupRequest request) {

        log.info("Starting signup process for email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {

            log.warn("Signup attempt failed - email already registered: {}", request.email());

            throw new IllegalArgumentException("Email already registered");
        }

        UserEntity user = UserEntity.builder()
                .uuid(UUID.generate())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .status("PENDING")
                .createdBy("system")
                .build();

        userRepository.save(user);

        log.info("User created successfully with UUID: {}", user.getUuid());

        final String otp = otpService.generate(user.getUuid(), OTPType.SIGNUP);

        log.info("OTP generated for user: {}", user.getUuid());

        loggingOTPNotificationServiceImpl.sendOtp(user.getEmail(), otp);

        log.info("OTP notification sent to email: {}", user.getEmail());

        log.info("Signup process completed successfully for email: {}", request.email());

        return new GenericResponse("Signup successful");
    }

    @Transactional
    @Override
    public GenericResponse verifyOtp(OtpVerificationRequest request) {

        log.info("Starting OTP verification for email: {}", request.email());

        Optional<UserEntity> optUserEntity = userRepository.findByEmail(request.email());

        if (optUserEntity.isEmpty()) {

            log.warn("OTP verification failed - email not found: {}", request.email());

            throw new IllegalArgumentException("Invalid OTP");
        }

        UserEntity userEntity = optUserEntity.get();

        boolean verify = otpService.verify(userEntity.getUuid(), OTPType.SIGNUP, request.otp());

        if (!verify) {

            log.warn("OTP verification failed for email: {}", request.email());

            throw new IllegalArgumentException("Invalid OTP");
        }

        userEntity.setStatus("ACTIVE");
        userEntity.setVerifiedAt(LocalDateTime.now());
        userEntity.setUpdatedBy("system");

        userRepository.save(userEntity);

        log.info("OTP verified successfully for email: {}", request.email());

        return new GenericResponse("OTP verified successfully");
    }

    @Override
    public GenericResponse resendOtp(ResendOtpRequest request) {
        return null;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public GenericResponse forgotPassword(ForgotPasswordRequest request) {
        return null;
    }

    @Override
    public GenericResponse resetPassword(ResetPasswordRequest request) {
        return null;
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }
}
