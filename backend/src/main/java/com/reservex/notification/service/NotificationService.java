package com.reservex.notification.service;

import com.reservex.notification.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void createNotification(
            UUID userId,
            String title,
            String message
    );

    List<NotificationResponse> getNotifications(
            UUID userId
    );
}