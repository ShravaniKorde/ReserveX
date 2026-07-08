package com.reservex.kafka.producer;

import com.reservex.config.KafkaConfig;
import com.reservex.kafka.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<
            String,
            BookingCreatedEvent
            > kafkaTemplate;

    /**
     * Publishes BookingCreatedEvent
     * to Kafka.
     */
    public void publish(
            BookingCreatedEvent event
    ) {

        log.info(
                "Publishing booking event. bookingId={}",
                event.getBookingId()
        );

        kafkaTemplate.send(
                KafkaConfig.BOOKING_CREATED_TOPIC,
                event
        );

        log.info(
                "Booking event published successfully. bookingId={}",
                event.getBookingId()
        );
    }
}