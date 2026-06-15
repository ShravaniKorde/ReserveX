package com.reservex.notification.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private String notificationId;

    private String title;

    private String message;

    private boolean read;

    private String createdAt;
}