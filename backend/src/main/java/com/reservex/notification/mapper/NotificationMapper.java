package com.reservex.notification.mapper;

import com.reservex.notification.dto.response.NotificationResponse;
import com.reservex.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(
            Notification notification
    ) {

        return NotificationResponse.builder()
                .notificationId(
                        notification.getId().toString()
                )
                .title(
                        notification.getTitle()
                )
                .message(
                        notification.getMessage()
                )
                .read(
                        notification.isRead()
                )
                .createdAt(
                        notification.getCreatedAt().toString()
                )
                .build();
    }
}