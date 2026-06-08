package com.reservex.auth.service.impl;

import com.reservex.auth.dto.request.LoginRequest;
import com.reservex.auth.dto.request.RegisterRequest;
import com.reservex.auth.dto.response.LoginResponse;
import com.reservex.auth.dto.response.RegisterResponse;
import com.reservex.auth.dto.response.UserProfileResponse;
import com.reservex.auth.entity.User;
import com.reservex.auth.exception.AuthException;
import com.reservex.auth.mapper.UserMapper;
import com.reservex.auth.repository.UserRepository;
import com.reservex.auth.security.JwtUtil;
import com.reservex.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    // ── POST /api/v1/auth/register ────────────────────────────────────────────

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // 1. Duplicate email check → 409
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AuthException.emailAlreadyExists();
        }

        // 2. Duplicate phone check → 409
        if (userRepository.existsByPhone(request.getPhone())) {
            throw AuthException.phoneAlreadyExists();
        }

        // 3. Hash password with bcrypt cost 12 (configured in SecurityConfig)
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // 4. Persist user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .phone(request.getPhone())
                .passwordHash(passwordHash)
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {} ({})", saved.getEmail(), saved.getId());

        // 5. Map to response DTO
        return userMapper.toRegisterResponse(saved);
    }

    // ── POST /api/v1/auth/login ───────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 1. Look up user by email
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(AuthException::invalidCredentials);

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw AuthException.invalidCredentials();
        }

        // 3. Generate JWT pair
        String accessToken  = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        log.info("User logged in: {} ({})", user.getEmail(), user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getAccessExpirySeconds())
                .tokenType("Bearer")
                .build();
    }

    // ── GET /api/v1/auth/me ───────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMe(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(AuthException::userNotFound);

        return userMapper.toProfileResponse(user);
    }
}