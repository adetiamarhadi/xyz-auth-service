package com.github.adetiamarhadi.xyz_auth_service.otp.impl;

import com.github.adetiamarhadi.xyz_auth_service.otp.OTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

import static com.github.adetiamarhadi.xyz_auth_service.util.Constant.OTP_LENGTH;

@Component("simpleRandomOTPGenerator")
public class SimpleRandomOTPGenerator implements OTPGenerator {

    private final Random random = new SecureRandom();

    @Override
    public String generate(String userUuid, OTPType otpType) {

        int number = random.nextInt((int) Math.pow(10, OTP_LENGTH));

        return String.format("%0" + OTP_LENGTH + "d", number);
    }
}
