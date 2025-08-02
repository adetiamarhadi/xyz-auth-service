package com.github.adetiamarhadi.xyz_auth_service.controller;

import com.github.adetiamarhadi.xyz_auth_service.dto.ForgotPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.GenericResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.LoginResponse;
import com.github.adetiamarhadi.xyz_auth_service.dto.OtpVerificationRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.RefreshTokenRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResendOtpRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.ResetPasswordRequest;
import com.github.adetiamarhadi.xyz_auth_service.dto.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signup successful",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<GenericResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(new GenericResponse("Signup mock successful"));
    }

    @Operation(summary = "Verify OTP sent to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class)))
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<GenericResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(new GenericResponse("OTP verification mock successful"));
    }

    @Operation(summary = "Resend OTP to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP resent successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class)))
    })
    @PostMapping("/resend-otp")
    public ResponseEntity<GenericResponse> resendOtp(@RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(new GenericResponse("Resend OTP mock successful"));
    }

    @Operation(summary = "Authenticate user and return tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new LoginResponse("mock-access-token", "mock-refresh-token"));
    }

    @Operation(summary = "Send forgot password link to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forgot password link sent",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class)))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(new GenericResponse("Password reset link sent mock"));
    }

    @Operation(summary = "Reset password using token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class)))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(new GenericResponse("Password reset mock successful"));
    }

    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(new LoginResponse("newAccessTokenMock", request.getRefreshToken()));
    }
}

