package com.reservex.notification.controller;

import com.reservex.common.response.ApiResponse;
import com.reservex.notification.dto.response.NotificationResponse;
import com.reservex.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>>
    getNotifications(
            @AuthenticationPrincipal String userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        notificationService.getNotifications(
                                UUID.fromString(userId)
                        )
                )
        );
    }
}