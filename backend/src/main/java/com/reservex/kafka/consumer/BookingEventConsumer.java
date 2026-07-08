package com.reservex.kafka.consumer;

import com.reservex.config.KafkaConfig;
import com.reservex.kafka.event.BookingCreatedEvent;
import com.reservex.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final NotificationService notificationService;

    /**
     * Consumes BookingCreatedEvent
     * and creates notification.
     */
    @KafkaListener(
            topics = KafkaConfig.BOOKING_CREATED_TOPIC,
            groupId = KafkaConfig.BOOKING_GROUP
    )
    public void consume(
            BookingCreatedEvent event
    ) {

        log.info(
                "Booking event received. bookingId={}, userId={}",
                event.getBookingId(),
                event.getUserId()
        );

        notificationService.createNotification(
                UUID.fromString(event.getUserId()),
                "Booking Confirmed",
                "Your booking has been confirmed."
        );

        log.info(
                "Notification created successfully. bookingId={}",
                event.getBookingId()
        );
    }
}