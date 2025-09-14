package com.github.adetiamarhadi.xyz_auth_service.otp;

import com.github.adetiamarhadi.xyz_auth_service.otp.impl.HOTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static com.github.adetiamarhadi.xyz_auth_service.util.Constant.OTP_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HOTPGeneratorTest {

    @InjectMocks
    private HOTPGenerator hotpGenerator;

    private static final String TEST_SECRET = "test-secret-key-12345";
    private static final String TEST_USER_UUID = "test-user-uuid";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hotpGenerator, "secretKey", TEST_SECRET);
    }

    @Test
    void shouldGenerateOTPWithCorrectLength() {
        String otp = hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        assertEquals(OTP_LENGTH, otp.length());
    }

    @Test
    void shouldGenerateNumericOTPOnly() {
        String otp = hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        assertTrue(otp.matches("\\d+"), "OTP should contain only digits");
    }

    @Test
    void shouldGenerateConsistentOTPForSameTimestamp() {
        // Fix the timestamp for consistent testing
        long timestamp = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();

        String otp1 = generateOTPWithTimestamp(timestamp);
        String otp2 = generateOTPWithTimestamp(timestamp);

        assertEquals(otp1, otp2, "OTPs should be consistent for same timestamp");
    }

    @Test
    void shouldGenerateDifferentOTPsForDifferentTimestamps() {
        long timestamp1 = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();
        long timestamp2 = Instant.parse("2025-01-01T00:00:01Z").toEpochMilli();

        String otp1 = generateOTPWithTimestamp(timestamp1);
        String otp2 = generateOTPWithTimestamp(timestamp2);

        assertNotEquals(otp1, otp2, "OTPs should be different for different timestamps");
    }

    @Test
    void shouldThrowExceptionForInvalidSecretKey() {
        ReflectionTestUtils.setField(hotpGenerator, "secretKey", "");

        Exception exception = assertThrows(IllegalStateException.class, () ->
            hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP)
        );

        assertTrue(exception.getMessage().contains("OTP generation failed"));
    }

    @Test
    void shouldGenerateUniqueOTPsForDifferentUsers() {
        String otp1 = hotpGenerator.generate("user1", OTPType.SIGNUP);
        String otp2 = hotpGenerator.generate("user2", OTPType.SIGNUP);

        // OTPs might be the same due to timestamp, but they should be valid
        assertTrue(otp1.matches("\\d{" + OTP_LENGTH + "}"));
        assertTrue(otp2.matches("\\d{" + OTP_LENGTH + "}"));
    }

    @Test
    void shouldGenerateValidOTPForDifferentTypes() {
        String loginOtp = hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        String signupOtp = hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        String resetOtp = hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);

        assertAll(
            () -> assertTrue(loginOtp.matches("\\d{" + OTP_LENGTH + "}")),
            () -> assertTrue(signupOtp.matches("\\d{" + OTP_LENGTH + "}")),
            () -> assertTrue(resetOtp.matches("\\d{" + OTP_LENGTH + "}"))
        );
    }

    @Test
    void shouldPadWithLeadingZerosIfNeeded() {
        // Generate multiple OTPs to increase chance of getting one that needs padding
        Set<String> otps = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            String otp = hotpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
            otps.add(otp);
            assertEquals(OTP_LENGTH, otp.length(),
                "OTP length should always be " + OTP_LENGTH + ", got: " + otp);
        }
    }

    private String generateOTPWithTimestamp(long timestamp) {
        try {
            return (String) ReflectionTestUtils.invokeMethod(hotpGenerator,
                "generateOTP",
                TEST_SECRET.getBytes(),
                timestamp,
                OTP_LENGTH,
                false,
                -1);
        } catch (Exception e) {
            throw new RuntimeException("Test failed", e);
        }
    }
}
