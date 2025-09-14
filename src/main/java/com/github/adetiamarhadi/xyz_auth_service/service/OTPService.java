package com.github.adetiamarhadi.xyz_auth_service.service;

import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;

public interface OTPService {

    /**
     * Generate new OTP for a user.
     *
     * @param userUuid the UUID of the user
     * @param otpType the type of OTP
     * @return the plain OTP (before hashing), to be delivered to the user
     */
    String generate(String userUuid, OTPType otpType);

    /**
     * Verify provided OTP.
     *
     * @param userUuid the UUID of the user
     * @param otpType the type of OTP
     * @param otpInput the OTP provided by the user
     * @return true if valid, false otherwise
     * @throws IllegalArgumentException if expired, used, or invalid
     */
    boolean verify(String userUuid, OTPType otpType, String otpInput);
}
