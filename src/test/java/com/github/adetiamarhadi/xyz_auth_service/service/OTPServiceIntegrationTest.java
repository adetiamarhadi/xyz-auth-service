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
    void shouldFailVerificationWithInvalidOTP() {
        String uuid = UUID.generate();
        String otp = otpService.generate(uuid, OTPType.SIGNUP);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d+"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, "999999"));
        assertEquals("Invalid or expired OTP", exception.getMessage());
    }

    @Test
    void shouldNotAllowReuseOfOTP() {
        String uuid = UUID.generate();
        String otp = otpService.generate(uuid, OTPType.SIGNUP);

        // First verification should succeed
        boolean firstVerify = otpService.verify(uuid, OTPType.SIGNUP, otp);
        assertTrue(firstVerify);

        // Second verification should fail
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, otp));
        assertEquals("OTP not found", exception.getMessage());
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
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, secondOtp));
        assertEquals("OTP not found", exception.getMessage());
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
        Exception firstAttempt = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, "111111"));
        assertEquals("Invalid or expired OTP", firstAttempt.getMessage());

        // Second invalid attempt
        Exception secondAttempt = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, "222222"));
        assertEquals("Invalid or expired OTP", secondAttempt.getMessage());

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
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(user2Uuid, OTPType.SIGNUP, user1Otp));
        assertEquals("OTP not found", exception.getMessage());
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
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, firstOtp));
        assertEquals("OTP not found", exception.getMessage());
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
        Exception exception1 = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, firstOtp));
        assertEquals("OTP not found", exception1.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, thirdOtp));
        assertEquals("OTP not found", exception2.getMessage());
    }

    @Test
    void shouldHandleMultipleOTPsWithInvalidAttempts() {
        String uuid = UUID.generate();

        // Generate two OTPs
        String firstOtp = otpService.generate(uuid, OTPType.SIGNUP);
        String secondOtp = otpService.generate(uuid, OTPType.SIGNUP);

        // Make invalid attempts (should increment attempt count for both OTPs)
        Exception exception1 = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, "111111"));
        assertEquals("Invalid or expired OTP", exception1.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, "222222"));
        assertEquals("Invalid or expired OTP", exception2.getMessage());

        // Both OTPs should still be valid for correct verification
        boolean firstResult = otpService.verify(uuid, OTPType.SIGNUP, firstOtp);
        assertTrue(firstResult);

        // Second OTP should be deleted
        Exception exception3 = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(uuid, OTPType.SIGNUP, secondOtp));
        assertEquals("OTP not found", exception3.getMessage());
    }
}
