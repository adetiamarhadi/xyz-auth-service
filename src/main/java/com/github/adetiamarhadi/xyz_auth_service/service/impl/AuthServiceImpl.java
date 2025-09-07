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
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import com.github.adetiamarhadi.xyz_auth_service.service.AuthService;
import com.github.adetiamarhadi.xyz_auth_service.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public GenericResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
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

        return new GenericResponse("Signup successful");
    }

    @Override
    public GenericResponse verifyOtp(OtpVerificationRequest request) {
        return null;
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
