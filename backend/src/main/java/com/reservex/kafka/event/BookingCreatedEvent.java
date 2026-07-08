package com.reservex.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {

    private String bookingId;

    private String userId;

    private String showId;

    private String bookingStatus;

    private String totalAmount;

    private String bookedAt;
}