package com.github.adetiamarhadi.xyz_auth_service.dto;

public record OtpVerificationRequest(
        String email,
        String otp
) {}
