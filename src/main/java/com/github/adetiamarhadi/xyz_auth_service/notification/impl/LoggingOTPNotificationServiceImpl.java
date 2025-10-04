package com.github.adetiamarhadi.xyz_auth_service.notification.impl;

import com.github.adetiamarhadi.xyz_auth_service.notification.OTPNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("loggingOTPNotificationServiceImpl")
public class LoggingOTPNotificationServiceImpl implements OTPNotificationService {

    @Override
    public void sendOtp(String recipient, String otp) {
        log.info("[MOCK OTP] Sending OTP -> recipient: {}, otp: {}", recipient, otp);
    }
}
