package com.reservex.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookingResponse {

    private String bookingId;

    private String showId;

    private String bookingStatus;

    private String totalAmount;

    private String bookedAt;

    private List<BookingSeatResponse> seats;
}