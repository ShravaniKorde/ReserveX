package com.reservex.booking.service;

import com.reservex.booking.dto.response.BookingResponse;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponse createBooking(
            UUID userId,
            UUID showId
    );

    BookingResponse getBooking(
            UUID bookingId,
            UUID userId
    );

    List<BookingResponse> getBookings(
            UUID userId
    );
}