package com.github.adetiamarhadi.xyz_auth_service.controller;

import com.github.adetiamarhadi.xyz_auth_service.config.TestContainersConfig;
import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.OtpVerificationRequest;
import com.github.adetiamarhadi.xyz_auth_service.entity.UserEntity;
import com.github.adetiamarhadi.xyz_auth_service.entity.UserOTPEntity;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserOTPRepository;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import com.github.adetiamarhadi.xyz_auth_service.type.OTPType;
import com.github.adetiamarhadi.xyz_auth_service.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-containers")
class VerifyOtpControllerTest extends TestContainersConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOTPRepository userOTPRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    @Test
    void verifyOtp_ShouldActivateUser_WhenValidOtp() {
        // given
        final String email = "verify.int@example.com";
        final String otp = "112233";

        UserEntity user = UserEntity.builder()
                .uuid(UUID.generate())
                .email(email)
                .password("encoded-password")
                .status("PENDING")
                .createdBy("system")
                .build();
        userRepository.save(user);

        UserOTPEntity otpEntity = UserOTPEntity.builder()
                .userUuid(user.getUuid())
                .otpType(OTPType.SIGNUP.name())
                .otpCodeHash(passwordEncoder.encode(otp))
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .attemptCount(0)
                .createdBy("system")
                .build();
        userOTPRepository.save(otpEntity);

        // when
        OtpVerificationRequest request = new OtpVerificationRequest(email, otp);
        ResponseEntity<GenericResponse> response = restTemplate.postForEntity(
                baseUrl() + "/verify-otp",
                request,
                GenericResponse.class
        );

        // then response
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("OTP verified successfully");

        // then db
        UserEntity updated = userRepository.findByEmail(email).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStatus()).isEqualTo("ACTIVE");
        assertThat(updated.getVerifiedAt()).isNotNull();
        assertThat(updated.getUpdatedBy()).isEqualTo("system");

        // ensure no unused OTPs remain
        assertThat(userOTPRepository.findAllByUserUuidAndOtpTypeAndUsedAtIsNull(user.getUuid(), OTPType.SIGNUP.name()))
                .isEmpty();
    }

    @Test
    void verifyOtp_ShouldReturnConflict_WhenOtpInvalid() {
        // given
        final String email = "verify.invalid@example.com";

        UserEntity user = UserEntity.builder()
                .uuid(UUID.generate())
                .email(email)
                .password("encoded-password")
                .status("PENDING")
                .createdBy("system")
                .build();
        userRepository.save(user);

        // prepare a different OTP in DB so that input becomes invalid
        UserOTPEntity otpEntity = UserOTPEntity.builder()
                .userUuid(user.getUuid())
                .otpType(OTPType.SIGNUP.name())
                .otpCodeHash(passwordEncoder.encode("000000"))
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .attemptCount(0)
                .createdBy("system")
                .build();
        userOTPRepository.save(otpEntity);

        // when + then
        OtpVerificationRequest badRequest = new OtpVerificationRequest(email, "123999");

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.Conflict.class, () -> {
            restTemplate.postForEntity(baseUrl() + "/verify-otp", badRequest, GenericResponse.class);
        });

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getResponseBodyAsString()).contains("Invalid OTP");

        // user should remain not active
        UserEntity notUpdated = userRepository.findByEmail(email).orElse(null);
        assertThat(notUpdated).isNotNull();
        assertThat(notUpdated.getStatus()).isEqualTo("PENDING");
        assertThat(notUpdated.getVerifiedAt()).isNull();
    }
}
