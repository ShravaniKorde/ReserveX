package com.reservex.kafka.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.reservex.booking.entity.Booking;
import com.reservex.config.KafkaConfig;
import com.reservex.kafka.event.BookingCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.reservex.booking.repository.BookingRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

       private final BookingRepository bookingRepository;
       
    @KafkaListener(
            topics = KafkaConfig.BOOKING_CREATED_TOPIC,
            groupId = "payment-group"
    )
    public void consume(
            BookingCreatedEvent event
    ) {

        UUID bookingId =
        UUID.fromString(
                event.getBookingId()
        );

    Booking booking =
        bookingRepository.findById(
                bookingId
        ).orElseThrow();

        log.info(
                "Received booking event. bookingId={}",
                event.getBookingId()
        );

        log.info(
        "Booking loaded successfully. bookingId={}",
        booking.getId()
        );

    }
}