package com.github.adetiamarhadi.xyz_auth_service.otp;

import com.github.adetiamarhadi.xyz_auth_service.otp.impl.TOTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TOTPGeneratorTest {

    @InjectMocks
    private TOTPGenerator totpGenerator;

    private static final String TEST_SECRET = "3132333435363738393031323334353637383930";
    private static final String TEST_USER_UUID = "test-user-uuid";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(totpGenerator, "secretKey", TEST_SECRET);
    }

    @Test
    void shouldGenerateSixDigitOTP() {
        String otp = totpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        assertEquals(6, otp.length(), "OTP should be 6 digits long");
        assertTrue(otp.matches("\\d{6}"), "OTP should contain only digits");
    }

    @Test
    void shouldGenerateDifferentOTPsForDifferentTimes() {
        Set<String> generatedOTPs = new HashSet<>();
        // Generate OTPs with different timestamps
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1); // Ensure different timestamps
                String otp = totpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
                generatedOTPs.add(otp);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Test interrupted");
            }
        }
        assertTrue(generatedOTPs.size() > 1, "Should generate different OTPs for different times");
    }

    @Test
    void shouldGenerateValidOTPForDifferentTypes() {
        String loginOtp = totpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        String signupOtp = totpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);
        String resetOtp = totpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP);

        assertAll(
            () -> assertTrue(loginOtp.matches("\\d{6}")),
            () -> assertTrue(signupOtp.matches("\\d{6}")),
            () -> assertTrue(resetOtp.matches("\\d{6}"))
        );
    }
}
