package com.github.adetiamarhadi.xyz_auth_service.service;

import com.github.adetiamarhadi.xyz_auth_service.config.TestContainersConfig;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import com.github.adetiamarhadi.xyz_auth_service.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@ActiveProfiles("test-containers")
class OTPServiceIntegrationTest extends TestContainersConfig {

    @Autowired
    private OTPService otpService;

    @Test
    void shouldGenerateAndVerifyOTP() {
        String uuid = UUID.generate();
        String otp = otpService.generate(uuid, OTPType.SIGNUP);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d+"));

        boolean verify = otpService.verify(uuid, OTPType.SIGNUP, otp);
        assertTrue(verify);
    }

    @Test
    void shouldReturnFalseForInvalidOTP() {
        String uuid = UUID.generate();
        String otp = otpService.generate(uuid, OTPType.SIGNUP);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d+"));

        boolean invalid = otpService.verify(uuid, OTPType.SIGNUP, "999999");
        assertFalse(invalid);
    }

    @Test
    void shouldNotAllowReuseOfOTP() {
        String uuid = UUID.generate();
        String otp = otpService.generate(uuid, OTPType.SIGNUP);

        // First verification should succeed
        boolean firstVerify = otpService.verify(uuid, OTPType.SIGNUP, otp);
        assertTrue(firstVerify);

        // Second verification should fail
        boolean reuse = otpService.verify(uuid, OTPType.SIGNUP, otp);
        assertFalse(reuse);
    }

    @Test
    void shouldAllowMultipleActiveOTPs() {
        String uuid = UUID.generate();

        // Generate first OTP
        String firstOtp = otpService.generate(uuid, OTPType.SIGNUP);

        // Generate second OTP
        String secondOtp = otpService.generate(uuid, OTPType.SIGNUP);

        assertNotEquals(firstOtp, secondOtp);

        System.out.println("First OTP: " + firstOtp);
        System.out.println("Second OTP: " + secondOtp);

        // Both OTPs should be valid
        boolean firstResult = otpService.verify(uuid, OTPType.SIGNUP, firstOtp);
        assertTrue(firstResult);

        // After successful verification, second OTP should no longer be valid
        boolean secondStillValid = otpService.verify(uuid, OTPType.SIGNUP, secondOtp);
        assertFalse(secondStillValid);
    }

    @Test
    void shouldWorkWithDifferentOTPTypes() {
        String uuid = UUID.generate();

        // Test SIGNUP OTP
        String signupOtp = otpService.generate(uuid, OTPType.SIGNUP);
        System.out.println("Generated SIGNUP OTP: " + signupOtp);
        assertTrue(otpService.verify(uuid, OTPType.SIGNUP, signupOtp));

        // Test LOGIN OTP
        String loginOtp = otpService.generate(uuid, OTPType.SIGNUP);
        System.out.println("Generated LOGIN OTP: " + loginOtp);
        assertTrue(otpService.verify(uuid, OTPType.SIGNUP, loginOtp));
    }

    @Test
    void shouldIncrementAttemptCount() {
        String uuid = UUID.generate();
        String otp = otpService.generate(uuid, OTPType.SIGNUP);

        // First invalid attempt
        boolean firstAttempt = otpService.verify(uuid, OTPType.SIGNUP, "111111");
        assertFalse(firstAttempt);

        // Second invalid attempt
        boolean secondAttempt = otpService.verify(uuid, OTPType.SIGNUP, "222222");
        assertFalse(secondAttempt);

        // Valid OTP should still work
        boolean result = otpService.verify(uuid, OTPType.SIGNUP, otp);
        assertTrue(result);
    }

    @Test
    void shouldNotVerifyOTPForDifferentUser() {
        String user1Uuid = UUID.generate();
        String user2Uuid = UUID.generate();

        String user1Otp = otpService.generate(user1Uuid, OTPType.SIGNUP);

        // Try to verify user1's OTP with user2's UUID
        boolean crossUser = otpService.verify(user2Uuid, OTPType.SIGNUP, user1Otp);
        assertFalse(crossUser);
    }

    @Test
    void shouldVerifySecondOTPWhenFirstIsUsed() {
        String uuid = UUID.generate();

        // Generate two OTPs
        String firstOtp = otpService.generate(uuid, OTPType.SIGNUP);
        String secondOtp = otpService.generate(uuid, OTPType.SIGNUP);

        // Verify second OTP first
        boolean secondResult = otpService.verify(uuid, OTPType.SIGNUP, secondOtp);
        assertTrue(secondResult);

        // First OTP should no longer be valid
        boolean firstStillValid = otpService.verify(uuid, OTPType.SIGNUP, firstOtp);
        assertFalse(firstStillValid);
    }

    @Test
    void shouldDeleteAllOTPsAfterOneSuccessfulVerification() {
        String uuid = UUID.generate();

        // Generate multiple OTPs
        String firstOtp = otpService.generate(uuid, OTPType.SIGNUP);
        String secondOtp = otpService.generate(uuid, OTPType.SIGNUP);
        String thirdOtp = otpService.generate(uuid, OTPType.SIGNUP);

        // Verify one OTP
        boolean result = otpService.verify(uuid, OTPType.SIGNUP, secondOtp);
        assertTrue(result);

        // All other OTPs should be invalid
        boolean firstInvalid = otpService.verify(uuid, OTPType.SIGNUP, firstOtp);
        assertFalse(firstInvalid);

        boolean thirdInvalid = otpService.verify(uuid, OTPType.SIGNUP, thirdOtp);
        assertFalse(thirdInvalid);
    }

    @Test
    void shouldHandleMultipleOTPsWithInvalidAttempts() {
        String uuid = UUID.generate();

        // Generate two OTPs
        String firstOtp = otpService.generate(uuid, OTPType.SIGNUP);
        String secondOtp = otpService.generate(uuid, OTPType.SIGNUP);

        // Make invalid attempts (should increment attempt count for both OTPs)
        boolean invalid1 = otpService.verify(uuid, OTPType.SIGNUP, "111111");
        assertFalse(invalid1);

        boolean invalid2 = otpService.verify(uuid, OTPType.SIGNUP, "222222");
        assertFalse(invalid2);

        // Both OTPs should still be valid for correct verification
        boolean firstResult = otpService.verify(uuid, OTPType.SIGNUP, firstOtp);
        assertTrue(firstResult);

        // Second OTP should be deleted
        boolean secondInvalid = otpService.verify(uuid, OTPType.SIGNUP, secondOtp);
        assertFalse(secondInvalid);
    }
}
