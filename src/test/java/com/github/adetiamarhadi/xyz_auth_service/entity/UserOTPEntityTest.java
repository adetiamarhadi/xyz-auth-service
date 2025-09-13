package com.github.adetiamarhadi.xyz_auth_service.entity;

import com.github.adetiamarhadi.xyz_auth_service.config.JpaAuditingConfiguration;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserOTPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfiguration.class)
@ActiveProfiles("test")
class UserOTPEntityTest {

    @Autowired
    private UserOTPRepository userOtpRepository;

    private LocalDateTime now;
    private UserOTPEntity userOtpEntity;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        userOtpEntity = UserOTPEntity.builder()
                .userUuid("user-uuid-123")
                .otpCodeHash("hash123")
                .otpType("LOGIN")
                .usedAt(now)
                .attemptCount(2)
                .expiredAt(now.plusMinutes(5))
                .build();
    }

    @Test
    void shouldBuildOTPEntityWithBuilder() {
        assertEquals("user-uuid-123", userOtpEntity.getUserUuid());
        assertEquals("hash123", userOtpEntity.getOtpCodeHash());
        assertEquals("LOGIN", userOtpEntity.getOtpType());
        assertEquals(now, userOtpEntity.getUsedAt());
        assertEquals(2, userOtpEntity.getAttemptCount());
        assertEquals(now.plusMinutes(5), userOtpEntity.getExpiredAt());
    }

    @Test
    void shouldSaveAndRetrieveOTPEntity() {
        UserOTPEntity saved = userOtpRepository.save(userOtpEntity);
        UserOTPEntity found = userOtpRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals(saved.getUserUuid(), found.getUserUuid());
        assertEquals(saved.getOtpCodeHash(), found.getOtpCodeHash());
        assertEquals(saved.getOtpType(), found.getOtpType());
        assertEquals(saved.getUsedAt(), found.getUsedAt());
        assertEquals(saved.getAttemptCount(), found.getAttemptCount());
        assertEquals(saved.getExpiredAt(), found.getExpiredAt());
    }

    @Test
    void shouldCompareEntitiesById() {
        UserOTPEntity saved1 = userOtpRepository.save(userOtpEntity);
        UserOTPEntity otp2 = UserOTPEntity.builder()
                .userUuid("user-uuid-456")
                .otpCodeHash("hash456")
                .otpType("SIGNUP")
                .usedAt(now)
                .attemptCount(3)
                .expiredAt(now.plusMinutes(10))
                .build();
        UserOTPEntity saved2 = userOtpRepository.save(otp2);
        assertNotEquals(saved1, saved2);
        assertEquals(saved1, userOtpRepository.findById(saved1.getId()).orElse(null));
    }
}
