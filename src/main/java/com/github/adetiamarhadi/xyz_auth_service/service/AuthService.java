package com.github.adetiamarhadi.xyz_auth_service.service;

import com.github.adetiamarhadi.xyz_auth_service.dto.ForgotPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.OtpVerificationRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.RefreshTokenRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResendOtpRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResetPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.SignupRequest;

public interface AuthService {

    /**
     * Registers a new user with the provided signup request data.
     *
     * <p>This method performs the following steps:
     * <ul>
     *     <li>Checks if the email in the request is already registered.</li>
     *     <li>If not, creates a new {@link UserEntity} with status "PENDING".</li>
     *     <li>Encodes the password using the configured {@link PasswordEncoder}.</li>
     *     <li>Persists the user in the database.</li>
     * </ul>
     *
     * @param request the signup request containing the user's email and password
     * @return a {@link GenericResponse} indicating success or failure
     * @throws IllegalArgumentException if the email is already registered
     */
    GenericResponse signup(SignupRequest request);

    GenericResponse verifyOtp(OtpVerificationRequest request);

    GenericResponse resendOtp(ResendOtpRequest request);

    LoginResponse login(LoginRequest request);

    GenericResponse forgotPassword(ForgotPasswordRequest request);

    GenericResponse resetPassword(ResetPasswordRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);
}
