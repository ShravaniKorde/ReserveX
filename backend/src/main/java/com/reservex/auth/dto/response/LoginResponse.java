package com.reservex.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response body for POST /api/v1/auth/login (200 OK)
 *
 * Shape from spec:
 * {
 *   "access_token":  "eyJ...",
 *   "refresh_token": "eyJ...",
 *   "expires_in":    3600,
 *   "token_type":    "Bearer"
 * }
 */
@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private long   expiresIn;    // seconds — always 3600 for the access token
    private String tokenType;    // always "Bearer"
}