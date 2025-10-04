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
     *     <li>Validates the signup request data.</li>
     *     <li>Checks if the email is already registered.</li>
     *     <li>Creates a new user account with pending status.</li>
     *     <li>Securely stores the user credentials.</li>
     *     <li>Initiates the account verification process.</li>
     * </ul>
     *
     * @param request the signup request containing the user's registration data
     * @return a {@link GenericResponse} indicating the result of the signup operation
     * @throws IllegalArgumentException if the email is already registered or request data is invalid
     */
    GenericResponse signup(SignupRequest request);

    GenericResponse verifyOtp(OtpVerificationRequest request);

    GenericResponse resendOtp(ResendOtpRequest request);

    LoginResponse login(LoginRequest request);

    GenericResponse forgotPassword(ForgotPasswordRequest request);

    GenericResponse resetPassword(ResetPasswordRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);
}
