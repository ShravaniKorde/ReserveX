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
import com.reservex.inventory.service.SeatLockRedisService;
import com.reservex.kafka.event.BookingCreatedEvent;
import com.reservex.kafka.producer.BookingEventProducer;

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
    private final BookingEventProducer bookingEventProducer;
    private final SeatLockRedisService seatLockRedisService;

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

                UUID redisShowId =
                        lock.getShowSeat()
                                .getShow()
                                .getId();

                UUID redisSeatId =
                        lock.getShowSeat()
                                .getSeat()
                                .getId();

                if (!seatLockRedisService.isLocked(
                        redisShowId,
                        redisSeatId
                )) {

                        throw new AppException(
                                HttpStatus.BAD_REQUEST,
                                "LOCK_EXPIRED",
                                "Seat lock has expired"
                        );
                }

                String owner =
                        seatLockRedisService.getLockOwner(
                                redisShowId,
                                redisSeatId
                        );

                if (!userId.toString().equals(owner)) {

                        throw new AppException(
                                HttpStatus.FORBIDDEN,
                                "LOCK_OWNERSHIP_MISMATCH",
                                "Seat lock belongs to another user"
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

                seatLockRedisService.unlockSeat(
                        showSeat.getShow().getId(),
                        showSeat.getSeat().getId()
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

        BookingCreatedEvent event =
                BookingCreatedEvent.builder()
                        .bookingId(
                                booking.getId().toString()
                        )
                        .userId(
                                userId.toString()
                        )
                        .showId(
                                showId.toString()
                        )
                        .bookingStatus(
                                booking.getBookingStatus().name()
                        )
                        .totalAmount(
                                booking.getTotalAmount().toString()
                        )
                        .bookedAt(
                                booking.getBookedAt().toString()
                        )
                        .build();

        bookingEventProducer.publish(event);

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