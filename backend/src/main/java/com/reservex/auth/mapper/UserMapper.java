package com.reservex.auth.mapper;

import com.reservex.auth.dto.response.RegisterResponse;
import com.reservex.auth.dto.response.UserProfileResponse;
import com.reservex.auth.entity.User;
import com.reservex.common.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * Converts User entity → response DTOs.
 * Kept as a plain Spring component (no MapStruct) to keep the build simple.
 */
@Component
public class UserMapper {

    public RegisterResponse toRegisterResponse(User user) {
        return RegisterResponse.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .message("Registration successful")
                .build();
    }

    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId().toString())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(DateUtil.formatUtc(user.getCreatedAt()))
                .build();
    }
}