package com.reservex.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base application exception.
 * Throw this (or a subclass) from any service layer.
 * GlobalExceptionHandler will map it to the correct HTTP status automatically.
 */
@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;
    private final String     errorCode;

    public AppException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status    = status;
        this.errorCode = errorCode;
    }
}