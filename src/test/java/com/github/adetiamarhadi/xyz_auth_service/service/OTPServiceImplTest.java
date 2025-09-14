package com.github.adetiamarhadi.xyz_auth_service.service;

import com.github.adetiamarhadi.xyz_auth_service.entity.UserOTPEntity;
import com.github.adetiamarhadi.xyz_auth_service.otp.OTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.otp.impl.SimpleRandomOTPGenerator;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserOTPRepository;
import com.github.adetiamarhadi.xyz_auth_service.service.impl.OTPServiceImpl;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OTPServiceImplTest {

    @Mock
    private UserOTPRepository userOTPRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OTPGenerator totpGenerator;

    @Mock
    private SimpleRandomOTPGenerator simpleRandomOTPGenerator;  // Changed to be explicit about implementation

    @InjectMocks
    private OTPServiceImpl otpService;

    @Captor
    private ArgumentCaptor<UserOTPEntity> otpEntityCaptor;

    private Map<String, OTPGenerator> otpGenerators;
    private static final String TEST_USER_UUID = "test-user-uuid";
    private static final String TEST_OTP = "123456";
    private static final String HASHED_OTP = "hashed-otp";

    @BeforeEach
    void setUp() {
        otpGenerators = new HashMap<>();
        otpGenerators.put("totpGenerator", totpGenerator);
        otpGenerators.put("simpleRandomOTPGenerator", simpleRandomOTPGenerator);  // Updated key to match service implementation

        // Use reflection to set the map
        try {
            Field field = OTPServiceImpl.class.getDeclaredField("otpGenerators");
            field.setAccessible(true);
            field.set(otpService, otpGenerators);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldGenerateWithTOTPGenerator() {
        // Given
        otpGenerators.remove("simpleRandomOTPGenerator");  // Updated key to match service implementation
        when(totpGenerator.generate(TEST_USER_UUID, OTPType.SIGNUP)).thenReturn(TEST_OTP);
        when(passwordEncoder.encode(TEST_OTP)).thenReturn(HASHED_OTP);
        when(userOTPRepository.save(any(UserOTPEntity.class))).thenAnswer(i -> i.getArgument(0));

        // When
        String generatedOTP = otpService.generate(TEST_USER_UUID, OTPType.SIGNUP);

        // Then
        assertEquals(TEST_OTP, generatedOTP);
        verify(totpGenerator).generate(TEST_USER_UUID, OTPType.SIGNUP);
    }

    @Test
    void shouldVerifyValidOTP() {
        // Given
        UserOTPEntity entity = createValidOTPEntity();
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(TEST_USER_UUID, OTPType.SIGNUP.name()))
                .thenReturn(List.of(entity));
        when(passwordEncoder.matches(TEST_OTP, HASHED_OTP)).thenReturn(true);

        // When
        boolean result = otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, TEST_OTP);

        // Then
        assertTrue(result);
        verify(userOTPRepository).save(otpEntityCaptor.capture());
        assertNotNull(otpEntityCaptor.getValue().getUsedAt());
        verify(userOTPRepository).deleteAllUnusedByUserUuidAndOtpType(TEST_USER_UUID, OTPType.SIGNUP.name());
    }

    @Test
    void shouldThrowExceptionWhenOTPNotFound() {
        // Given
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, TEST_OTP));
        assertEquals("OTP not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenOTPExpired() {
        // Given
        UserOTPEntity entity = createExpiredOTPEntity();
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(TEST_USER_UUID, OTPType.SIGNUP.name()))
                .thenReturn(List.of(entity));
        when(passwordEncoder.matches(TEST_OTP, HASHED_OTP)).thenReturn(true);

        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, TEST_OTP));
        assertEquals("Invalid or expired OTP", exception.getMessage());
        verify(userOTPRepository).save(otpEntityCaptor.capture());
        assertEquals(1, otpEntityCaptor.getValue().getAttemptCount());
    }

    @Test
    void shouldNotFindAlreadyUsedOTP() {
        // Given - used OTPs should not be returned by findAllByUserUuidAndOtpTypeAndUsedAtIsNull
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(TEST_USER_UUID, OTPType.SIGNUP.name()))
                .thenReturn(Collections.emptyList());

        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, TEST_OTP));
        assertEquals("OTP not found", exception.getMessage());
    }

    @Test
    void shouldIncrementAttemptCountOnInvalidOTP() {
        // Given
        UserOTPEntity entity = createValidOTPEntity();
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(TEST_USER_UUID, OTPType.SIGNUP.name()))
                .thenReturn(List.of(entity));
        when(passwordEncoder.matches(TEST_OTP, HASHED_OTP)).thenReturn(false);

        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, TEST_OTP));

        assertEquals("Invalid or expired OTP", exception.getMessage());
        verify(userOTPRepository).save(otpEntityCaptor.capture());
        assertEquals(1, otpEntityCaptor.getValue().getAttemptCount());
    }

    @Test
    void shouldVerifyFirstOfMultipleValidOTPs() {
        // Given
        UserOTPEntity entity1 = createValidOTPEntity();
        UserOTPEntity entity2 = createValidOTPEntityWithHash("another-hash");
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(TEST_USER_UUID, OTPType.SIGNUP.name()))
                .thenReturn(List.of(entity1, entity2));
        when(passwordEncoder.matches(TEST_OTP, HASHED_OTP)).thenReturn(true);
        when(passwordEncoder.matches(TEST_OTP, "another-hash")).thenReturn(false);

        // When
        boolean result = otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, TEST_OTP);

        // Then
        assertTrue(result);
        verify(userOTPRepository).save(otpEntityCaptor.capture());
        assertNotNull(otpEntityCaptor.getValue().getUsedAt());
        verify(userOTPRepository).deleteAllUnusedByUserUuidAndOtpType(TEST_USER_UUID, OTPType.SIGNUP.name());
    }

    @Test
    void shouldIncrementAttemptCountForAllOTPsOnInvalidInput() {
        // Given
        UserOTPEntity entity1 = createValidOTPEntity();
        UserOTPEntity entity2 = createValidOTPEntityWithHash("another-hash");
        when(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(TEST_USER_UUID, OTPType.SIGNUP.name()))
                .thenReturn(List.of(entity1, entity2));
        when(passwordEncoder.matches("999999", HASHED_OTP)).thenReturn(false);
        when(passwordEncoder.matches("999999", "another-hash")).thenReturn(false);

        // When/Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                otpService.verify(TEST_USER_UUID, OTPType.SIGNUP, "999999"));

        assertEquals("Invalid or expired OTP", exception.getMessage());
        verify(userOTPRepository, times(2)).save(any(UserOTPEntity.class));
    }

    private UserOTPEntity createValidOTPEntity() {
        return UserOTPEntity.builder()
                .userUuid(TEST_USER_UUID)
                .otpType(OTPType.SIGNUP.name())
                .otpCodeHash(HASHED_OTP)
                .expiredAt(LocalDateTime.now().plusMinutes(2))
                .attemptCount(0)
                .build();
    }

    private UserOTPEntity createExpiredOTPEntity() {
        return UserOTPEntity.builder()
                .userUuid(TEST_USER_UUID)
                .otpType(OTPType.SIGNUP.name())
                .otpCodeHash(HASHED_OTP)
                .expiredAt(LocalDateTime.now().minusMinutes(1))
                .attemptCount(0)
                .build();
    }

    private UserOTPEntity createValidOTPEntityWithHash(String hash) {
        return UserOTPEntity.builder()
                .userUuid(TEST_USER_UUID)
                .otpType(OTPType.SIGNUP.name())
                .otpCodeHash(hash)
                .expiredAt(LocalDateTime.now().plusMinutes(2))
                .attemptCount(0)
                .build();
    }

}
