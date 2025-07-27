package com.github.adetiamarhadi.xyz_auth_service.controller;

import com.github.adetiamarhadi.xyz_auth_service.dto.ForgotPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.OtpVerificationRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.RefreshTokenRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResendOtpRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResetPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<GenericResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(new GenericResponse("Signup mock successful"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(new GenericResponse("OTP verification mock successful"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<GenericResponse> resendOtp(@RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(new GenericResponse("Resend OTP mock successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new LoginResponse("mock-access-token", "mock-refresh-token"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(new GenericResponse("Password reset link sent mock"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(new GenericResponse("Password reset mock successful"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(new LoginResponse("newAccessTokenMock", request.getRefreshToken()));
    }
}

