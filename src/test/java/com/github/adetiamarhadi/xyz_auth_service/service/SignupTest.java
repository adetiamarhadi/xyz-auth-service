package com.github.adetiamarhadi.xyz_auth_service.service;

import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.SignupRequest;
import com.github.adetiamarhadi.xyz_auth_service.entity.UserEntity;
import com.github.adetiamarhadi.xyz_auth_service.repository.UserRepository;
import com.github.adetiamarhadi.xyz_auth_service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignupTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_ShouldThrowException_WhenEmailAlreadyExists() {

        // given
        SignupRequest request = new SignupRequest("test@example.com", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // when + then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.signup(request)
        );

        assertEquals("Email already registered", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_ShouldSaveUserAndReturnSuccess_WhenEmailNotExists() {

        // given
        SignupRequest request = new SignupRequest("new@example.com", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        // when
        GenericResponse response = authService.signup(request);

        // then
        assertNotNull(response);
        assertEquals("Signup successful", response.message());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();

        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("PENDING", savedUser.getStatus());
        assertEquals("system", savedUser.getCreatedBy());

        assertNotNull(savedUser.getUuid());
    }
}
