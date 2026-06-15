package com.reservex.booking.service.impl;

import com.reservex.booking.dto.response.BookingResponse;
import com.reservex.booking.entity.Booking;
import com.reservex.booking.entity.BookingSeat;
import com.reservex.booking.enums.BookingStatus;
import com.reservex.booking.mapper.BookingMapper;
import com.reservex.booking.repository.BookingRepository;
import com.reservex.booking.repository.BookingSeatRepository;
import com.reservex.booking.service.BookingService;
import com.reservex.common.exception.AppException;
import com.reservex.inventory.entity.SeatLock;
import com.reservex.inventory.entity.ShowSeat;
import com.reservex.inventory.enums.LockStatus;
import com.reservex.inventory.enums.SeatStatus;
import com.reservex.inventory.repository.SeatLockRepository;
import com.reservex.show.entity.Show;
import com.reservex.show.repository.ShowRepository;
import com.reservex.notification.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatLockRepository seatLockRepository;
    private final ShowRepository showRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

      /*
    Purpose:

    Convert locked seats into a confirmed booking.

    ACTIVE LOCKS
            ↓
    Validate locks
            ↓
    Create booking
            ↓
    Create booking seats
            ↓
    LOCKED → BOOKED
            ↓
    Return booking details
    */

        @Override
        @Transactional
        public BookingResponse createBooking(
                UUID userId,
                UUID showId
        ) {

        log.info(
                "Booking creation started. userId={}, showId={}",
                userId,
                showId
        );

        Show show =
                showRepository.findById(showId)
                        .orElseThrow(() ->
                                new AppException(
                                        HttpStatus.NOT_FOUND,
                                        "SHOW_NOT_FOUND",
                                        "Show not found"
                                )
                        );

        List<SeatLock> activeLocks =
                seatLockRepository
                        .findByUserIdAndShowSeat_Show_IdAndLockStatus(
                                userId,
                                showId,
                                LockStatus.ACTIVE
                        );

        if (activeLocks.isEmpty()) {

                throw new AppException(
                        HttpStatus.BAD_REQUEST,
                        "NO_ACTIVE_LOCKS",
                        "No active seat locks found"
                );
        }

        Instant now = Instant.now();

        for (SeatLock lock : activeLocks) {

                if (lock.getLockExpiryAt().isBefore(now)) {

                log.warn(
                        "Expired lock detected. lockId={}",
                        lock.getId()
                );

                throw new AppException(
                        HttpStatus.BAD_REQUEST,
                        "LOCK_EXPIRED",
                        "One or more seat locks have expired"
                );
                }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SeatLock lock : activeLocks) {

                totalAmount =
                        totalAmount.add(
                                lock.getShowSeat().getPrice()
                        );
        }

        Booking booking =
                bookingRepository.save(
                        Booking.builder()
                                .userId(userId)
                                .show(show)
                                .bookingStatus(
                                        BookingStatus.CONFIRMED
                                )
                                .totalAmount(totalAmount)
                                .bookedAt(now)
                                .build()
                );

        log.info(
                "Booking created successfully. bookingId={}",
                booking.getId()
        );

        for (SeatLock lock : activeLocks) {

                ShowSeat showSeat =
                        lock.getShowSeat();

                showSeat.setStatus(
                        SeatStatus.BOOKED
                );

                lock.setLockStatus(
                        LockStatus.RELEASED
                );

                bookingSeatRepository.save(
                        BookingSeat.builder()
                                .booking(booking)
                                .showSeat(showSeat)
                                .build()
                );
        }

        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBooking(
                        booking
                );

        log.info(
                "Booking completed successfully. bookingId={}, seatsBooked={}",
                booking.getId(),
                bookingSeats.size()
        );
        notificationService.createNotification(
        userId,
        "Booking Confirmed",
        "Your booking has been confirmed."
);

        return bookingMapper.toResponse(
                booking,
                bookingSeats
        );
        }

        /*
        Purpose:

        Fetch a single booking for the logged-in user.
        */
    @Override
    public BookingResponse getBooking(
            UUID bookingId,
            UUID userId
    ) {

        log.info(
                "Fetching booking. bookingId={}, userId={}",
                bookingId,
                userId
        );

        Booking booking =
                bookingRepository.findByIdAndUserId(
                        bookingId,
                        userId
                )
                        .orElseThrow(() ->
                                new AppException(
                                        HttpStatus.NOT_FOUND,
                                        "BOOKING_NOT_FOUND",
                                        "Booking not found"
                                )
                        );

        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBooking(
                        booking
                );

        return bookingMapper.toResponse(
                booking,
                bookingSeats
        );
    }

       /*
    Purpose:

    Fetch all bookings created by the logged-in user.
    */
   
    @Override
    public List<BookingResponse> getBookings(
            UUID userId
    ) {

        log.info(
                "Fetching all bookings for userId={}",
                userId
        );

        return bookingRepository.findByUserId(userId)
                .stream()
                .map(booking -> {

                    List<BookingSeat> bookingSeats =
                            bookingSeatRepository.findByBooking(
                                    booking
                            );

                    return bookingMapper.toResponse(
                            booking,
                            bookingSeats
                    );
                })
                .toList();
    }
}