package com.github.adetiamarhadi.xyz_auth_service.notification;

public interface OTPNotificationService {

    /**
     * Sends the generated OTP to the user.
     *
     * @param recipient the target address (e.g., email or phone)
     * @param otp the plain OTP code before hashing
     */
    void sendOtp(String recipient, String otp);
}
