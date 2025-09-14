package com.github.adetiamarhadi.xyz_auth_service.otp;

import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;

public interface OTPGenerator {

    /**
     * Generate OTP code
     *
     * @param userUuid user identifier
     * @param otpType type of OTP (e.g., SIGNUP, FORGOT_PASSWORD)
     * @return plain OTP (before hashing)
     */
    String generate(String userUuid, OTPType otpType);
}
