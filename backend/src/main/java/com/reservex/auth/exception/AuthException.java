package com.reservex.auth.exception;

import com.reservex.common.exception.AppException;
import org.springframework.http.HttpStatus;

/**
 * Auth-domain exceptions with named factory methods.
 * Keeps service code readable: throw AuthException.emailAlreadyExists()
 */
public class AuthException extends AppException {

    public AuthException(HttpStatus status, String errorCode, String message) {
        super(status, errorCode, message);
    }

    // ── factory methods ───────────────────────────────────────────────────────

    public static AuthException emailAlreadyExists() {
        return new AuthException(
            HttpStatus.CONFLICT,
            "EMAIL_ALREADY_EXISTS",
            "An account with this email address already exists"
        );
    }

    public static AuthException phoneAlreadyExists() {
        return new AuthException(
            HttpStatus.CONFLICT,
            "PHONE_ALREADY_EXISTS",
            "An account with this phone number already exists"
        );
    }

    public static AuthException invalidCredentials() {
        return new AuthException(
            HttpStatus.UNAUTHORIZED,
            "INVALID_CREDENTIALS",
            "Invalid email or password"
        );
    }

    public static AuthException userNotFound() {
        return new AuthException(
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            "User account not found"
        );
    }

    public static AuthException tokenInvalid() {
        return new AuthException(
            HttpStatus.UNAUTHORIZED,
            "TOKEN_INVALID",
            "Token is invalid or has expired"
        );
    }

    // Rate limit exception 

    public static AuthException loginRateLimitExceeded(String email) {
        return new AuthException(
            HttpStatus.TOO_MANY_REQUESTS,
            "RATE_LIMIT_EXCEEDED",
            "Too many login attempts for email: " + email + 
            ". Please try again after 10 minutes."
        );
    }
}