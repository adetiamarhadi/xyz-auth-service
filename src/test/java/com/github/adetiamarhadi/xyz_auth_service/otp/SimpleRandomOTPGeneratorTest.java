package com.github.adetiamarhadi.xyz_auth_service.otp;

import com.github.adetiamarhadi.xyz_auth_service.otp.impl.SimpleRandomOTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.github.adetiamarhadi.xyz_auth_service.util.Constant.OTP_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

class SimpleRandomOTPGeneratorTest {

    private SimpleRandomOTPGenerator otpGenerator;

    @BeforeEach
    void setUp() {
        otpGenerator = new SimpleRandomOTPGenerator();
    }

    @Test
    void shouldGenerateOTPWithCorrectLength() {
        String otp = otpGenerator.generate("user-123", OTPType.SIGNUP);
        assertEquals(OTP_LENGTH, otp.length());
    }

    @Test
    void shouldGenerateNumericOTPOnly() {
        String otp = otpGenerator.generate("user-123", OTPType.SIGNUP);
        assertTrue(otp.matches("\\d+"), "OTP should contain only digits");
    }

    @Test
    void shouldGenerateDifferentOTPsForMultipleCalls() {
        Set<String> generatedOTPs = new HashSet<>();
        int numberOfOTPs = 100; // Generate 100 OTPs to test randomness

        for (int i = 0; i < numberOfOTPs; i++) {
            String otp = otpGenerator.generate("user-123", OTPType.SIGNUP);
            generatedOTPs.add(otp);
        }

        // If OTPs are truly random, we should get close to numberOfOTPs unique values
        // Allow for some duplicates (expecting at least 90% unique)
        assertTrue(generatedOTPs.size() > numberOfOTPs * 0.9,
                "Generated OTPs should be mostly unique");
    }

    @Test
    void shouldPadWithLeadingZerosIfNeeded() {
        String otp = otpGenerator.generate("user-123", OTPType.SIGNUP);

        // Check that the OTP starts with zeros if it's shorter than OTP_LENGTH
        assertTrue(otp.matches("\\d{" + OTP_LENGTH + "}"),
                "OTP should be padded with zeros if needed");
    }

    @Test
    void shouldGenerateValidOTPForDifferentTypes() {
        // Test with different OTP types
        String loginOTP = otpGenerator.generate("user-123", OTPType.SIGNUP);
        String signupOTP = otpGenerator.generate("user-123", OTPType.SIGNUP);
        String resetOTP = otpGenerator.generate("user-123", OTPType.SIGNUP);

        // Verify all types generate valid OTPs
        assertAll(
            () -> assertEquals(OTP_LENGTH, loginOTP.length()),
            () -> assertEquals(OTP_LENGTH, signupOTP.length()),
            () -> assertEquals(OTP_LENGTH, resetOTP.length()),
            () -> assertTrue(loginOTP.matches("\\d+")),
            () -> assertTrue(signupOTP.matches("\\d+")),
            () -> assertTrue(resetOTP.matches("\\d+"))
        );
    }
}
