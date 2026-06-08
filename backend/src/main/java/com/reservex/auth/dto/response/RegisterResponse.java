package com.reservex.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response body for POST /api/v1/auth/register (201 Created)
 *
 * Shape from spec:
 * {
 *   "user_id": "USR001",
 *   "email":   "alice@example.com",
 *   "message": "Registration successful"
 * }
 */
@Getter
@Builder
public class RegisterResponse {

    private String userId;
    private String email;
    private String message;
}