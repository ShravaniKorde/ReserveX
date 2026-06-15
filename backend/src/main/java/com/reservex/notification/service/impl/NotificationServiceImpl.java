package com.reservex.notification.service.impl;

import com.reservex.notification.dto.response.NotificationResponse;
import com.reservex.notification.entity.Notification;
import com.reservex.notification.mapper.NotificationMapper;
import com.reservex.notification.repository.NotificationRepository;
import com.reservex.notification.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void createNotification(
            UUID userId,
            String title,
            String message
    ) {

        notificationRepository.save(
                Notification.builder()
                        .userId(userId)
                        .title(title)
                        .message(message)
                        .read(false)
                        .build()
        );
    }

    @Override
    public List<NotificationResponse> getNotifications(
            UUID userId
    ) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }
}