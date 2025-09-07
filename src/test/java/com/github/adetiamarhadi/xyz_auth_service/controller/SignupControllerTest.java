package com.github.adetiamarhadi.xyz_auth_service.controller;

import com.github.adetiamarhadi.xyz_auth_service.config.TestContainersConfig;
import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.SignupRequest;
import com.github.adetiamarhadi.xyz_auth_service.entity.UserEntity;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import com.github.adetiamarhadi.xyz_auth_service.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SignupControllerTest extends TestContainersConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    @Test
    void signup_ShouldFail_WhenEmailAlreadyExists() {

        final String email = "duplicate@example.com";
        final String password = "anotherSecret";

        // given: insert user lebih dulu ke DB
        UserEntity existing = UserEntity.builder()
                .uuid(UUID.generate())
                .email(email)
                .password("encoded-password")
                .status("ACTIVE")
                .build();
        userRepository.save(existing);

        // when
        SignupRequest request = new SignupRequest(email, password);

        // then (call API dan expect exception)
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.Conflict.class, () -> {
            restTemplate.postForEntity(baseUrl() + "/signup", request, GenericResponse.class);
        });

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(ex.getResponseBodyAsString().contains("Email already registered"));

        // pastikan user baru tidak ke-insert
        long count = userRepository.count();
        assertThat(count).isEqualTo(1); // cuma existing user
    }


    @Test
    void signup_ShouldSaveUserToDatabase() {

        String email = "integration@example.com";
        String password = "secret123";

        // given
        SignupRequest request = new SignupRequest(email, password);

        // when
        ResponseEntity<GenericResponse> response = restTemplate.postForEntity(
                baseUrl() + "/signup",
                request,
                GenericResponse.class
        );

        // then (assert response)
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Signup successful");

        // then (assert db)
        UserEntity savedUser = userRepository.findByEmail(email).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUuid()).isNotBlank();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isNotBlank();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getVersion()).isNotNull();
        assertThat(savedUser.getVerifiedAt()).isNull();
        assertThat(savedUser.getUpdatedBy()).isNull();
        assertThat(savedUser.getStatus()).isEqualTo("PENDING");
        assertThat(savedUser.getCreatedBy()).isEqualTo("system");
    }
}
