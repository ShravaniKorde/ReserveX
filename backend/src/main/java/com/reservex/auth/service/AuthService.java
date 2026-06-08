package com.reservex.auth.service;

import com.reservex.auth.dto.request.LoginRequest;
import com.reservex.auth.dto.request.RegisterRequest;
import com.reservex.auth.dto.response.LoginResponse;
import com.reservex.auth.dto.response.RegisterResponse;
import com.reservex.auth.dto.response.UserProfileResponse;

import java.util.UUID;

/**
 * Auth service contract.
 * Controller depends on this interface, not the implementation.
 */
public interface AuthService {

    /** Register a new user account. Throws AuthException on duplicate email/phone. */
    RegisterResponse register(RegisterRequest request);

    /** Authenticate credentials and return JWT pair. */
    LoginResponse login(LoginRequest request);

    /** Return the profile of the currently authenticated user. */
    UserProfileResponse getMe(UUID userId);
}