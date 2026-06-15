package com.reservex.booking.mapper;

import com.reservex.booking.dto.response.BookingResponse;
import com.reservex.booking.dto.response.BookingSeatResponse;
import com.reservex.booking.entity.Booking;
import com.reservex.booking.entity.BookingSeat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking, List<BookingSeat> bookingSeats) {

        return BookingResponse.builder()
                .bookingId(booking.getId().toString())
                .showId(booking.getShow().getId().toString())
                .bookingStatus(booking.getBookingStatus().name())
                .totalAmount(booking.getTotalAmount().toString())
                .bookedAt(booking.getBookedAt().toString())
                .seats(bookingSeats.stream()
                                .map(this::toSeatResponse)
                                .toList())
                .build();
    }

    private BookingSeatResponse toSeatResponse(BookingSeat bookingSeat) {

        return BookingSeatResponse.builder()
                .seatId(bookingSeat.getShowSeat()
                                .getSeat()
                                .getId()
                                .toString())
                .rowNumber(bookingSeat.getShowSeat()
                                .getSeat()
                                .getRowNumber())
                .seatNumber(bookingSeat.getShowSeat()
                                .getSeat()
                                .getSeatNumber())
                .price(bookingSeat.getShowSeat()
                                .getPrice()
                                .toString())
                .build();
    }
}