package com.reservex.auth.service;

import com.reservex.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Handles rate limiting logic using Redis.
 * Tracks login attempts per email and blocks if limit exceeded.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitHandler {

    private static final String KEY_PREFIX = "login:attempts:";
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 600;  // 10 minutes

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Check if email has exceeded rate limit.
     * If limit exceeded → throw AuthException.loginRateLimitExceeded().
     * If limit not exceeded → increment counter and return.
     */
    public void checkLoginRateLimit(String email) {
        String key = KEY_PREFIX + email;
        
        // Get current attempt count from Redis
        String attempts = stringRedisTemplate.opsForValue().get(key);
        int currentAttempts = attempts != null ? Integer.parseInt(attempts) : 0;
        
        // Check if limit exceeded
        if (currentAttempts >= MAX_ATTEMPTS) {
            throw AuthException.loginRateLimitExceeded(email);
        }
        
        // Increment attempt count
        int newAttempts = currentAttempts + 1;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(newAttempts), Duration.ofSeconds(WINDOW_SECONDS));
        
        log.info("Login attempt {} for email: {} (limit: {})", newAttempts, email, MAX_ATTEMPTS);
    }
}