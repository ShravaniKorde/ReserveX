package com.reservex.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * Every API response is wrapped in this envelope.
 *
 * Success shape : { "success": true,  "data": { ... } }
 * Error shape   : { "success": false, "error": { "code": "...", "message": "..." } }
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorBody error;

    // ── private constructors — use static factories below ────────────────────

    private ApiResponse(T data) {
        this.success = true;
        this.data    = data;
        this.error   = null;
    }

    private ApiResponse(String code, String message) {
        this.success = false;
        this.data    = null;
        this.error   = new ErrorBody(code, message);
    }

    // ── success factories ─────────────────────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data);
    }

    // ── error factory ─────────────────────────────────────────────────────────

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message);
    }

    // ── nested error body ─────────────────────────────────────────────────────

    public record ErrorBody(String code, String message) {}
}