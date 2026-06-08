package com.reservex.auth.controller;

import com.reservex.auth.dto.request.LoginRequest;
import com.reservex.auth.dto.request.RegisterRequest;
import com.reservex.auth.dto.response.LoginResponse;
import com.reservex.auth.dto.response.RegisterResponse;
import com.reservex.auth.dto.response.UserProfileResponse;
import com.reservex.auth.service.AuthService;
import com.reservex.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Handles:
 *   POST /api/v1/auth/register  — PUBLIC
 *   POST /api/v1/auth/login     — PUBLIC
 *   GET  /api/v1/auth/me        — AUTH  (JWT required)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── POST /api/v1/auth/register ────────────────────────────────────────────

    /**
     * Register a new user account.
     *
     * Returns 201 Created on success.
     * Returns 409 Conflict if email already exists.
     * Returns 400 Bad Request if validation fails.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    // ── POST /api/v1/auth/login ───────────────────────────────────────────────

    /**
     * Authenticate user credentials and return a JWT pair.
     *
     * Returns 200 OK with access_token + refresh_token.
     * Returns 401 Unauthorized if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── GET /api/v1/auth/me ───────────────────────────────────────────────────

    /**
     * Return the profile of the currently authenticated user.
     *
     * The user ID is extracted from the JWT by JwtAuthFilter and injected
     * by Spring Security as the @AuthenticationPrincipal (the principal is
     * the UUID string set in CustomUserDetailsService / JwtAuthFilter).
     *
     * Returns 200 OK with user profile.
     * Returns 401 if token is missing or invalid (handled by SecurityConfig).
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe(
            @AuthenticationPrincipal String userId
    ) {
        UserProfileResponse response = authService.getMe(UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}