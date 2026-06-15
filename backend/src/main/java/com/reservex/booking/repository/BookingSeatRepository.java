package com.reservex.booking.repository;

import com.reservex.booking.entity.Booking;
import com.reservex.booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingSeatRepository
        extends JpaRepository<BookingSeat, UUID> {

    List<BookingSeat> findByBooking_Id(
            UUID bookingId
    );

    List<BookingSeat> findByBooking(Booking booking);
}