package com.reservex.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Handles JWT creation and validation.
 *
 * Two token types:
 *   ACCESS  — short-lived (1 hr), carries user_id + role, used for API auth
 *   REFRESH — long-lived (30 d), used to obtain new access tokens
 *
 * Uses JJWT 0.12.x fluent builder API.
 */
@Slf4j
@Component
public class JwtUtil {

    private static final String CLAIM_ROLE     = "role";
    private static final String CLAIM_TYPE     = "type";
    private static final String TYPE_ACCESS    = "ACCESS";
    private static final String TYPE_REFRESH   = "REFRESH";

    private final SecretKey key;
    private final long      accessExpiryMs;
    private final long      refreshExpiryMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry-ms}") long accessExpiryMs,
            @Value("${jwt.refresh-token-expiry-ms}") long refreshExpiryMs
    ) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);


        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret must be at least 32 bytes"
            );
        }

this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpiryMs  = accessExpiryMs;
        this.refreshExpiryMs = refreshExpiryMs;
    }

    // ── token generation ──────────────────────────────────────────────────────

    public String generateAccessToken(UUID userId, String role) {
        return buildToken(userId.toString(), role, TYPE_ACCESS, accessExpiryMs);
    }

    public String generateRefreshToken(UUID userId) {
        return buildToken(userId.toString(), null, TYPE_REFRESH, refreshExpiryMs);
    }

    private String buildToken(String subject, String role, String type, long expiryMs) {
        long now = System.currentTimeMillis();

        var builder = Jwts.builder()
                .subject(subject)
                .claim(CLAIM_TYPE, type)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(key);

        if (role != null) {
            builder.claim(CLAIM_ROLE, role);
        }

        return builder.compact();
    }

    // ── token parsing ─────────────────────────────────────────────────────────

    /**
     * Parses and validates a token. Returns the Claims on success.
     * Throws JwtException if the token is invalid or expired.
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(Claims claims) {
        return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public String extractUserId(Claims claims) {
        return claims.getSubject();
    }

    public String extractRole(Claims claims) {
        return claims.get(CLAIM_ROLE, String.class);
    }

    /**
     * Safe validation — returns false instead of throwing.
     * Used by JwtAuthFilter.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return isAccessToken(claims);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /** Returns access token expiry in seconds (for the expires_in field in LoginResponse). */
    public long getAccessExpirySeconds() {
        return accessExpiryMs / 1000;
    }
}