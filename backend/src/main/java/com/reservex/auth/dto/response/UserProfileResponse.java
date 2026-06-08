package com.reservex.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response body for GET /api/v1/auth/me (200 OK)
 *
 * Shape from spec:
 * {
 *   "user_id":    "USR001",
 *   "full_name":  "Alice Smith",
 *   "email":      "alice@example.com",
 *   "phone":      "+919999999999",
 *   "created_at": "2026-06-01T00:00:00Z"
 * }
 */
@Getter
@Builder
public class UserProfileResponse {

    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String createdAt;   // ISO-8601 UTC string
}