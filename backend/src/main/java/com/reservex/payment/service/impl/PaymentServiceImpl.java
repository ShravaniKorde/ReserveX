package com.reservex.payment.service.impl;

import com.reservex.booking.entity.Booking;
import com.reservex.booking.repository.BookingRepository;
import com.reservex.common.exception.AppException;
import com.reservex.payment.dto.request.CreatePaymentRequest;
import com.reservex.payment.dto.request.PaymentWebhookRequest;
import com.reservex.payment.dto.response.PaymentResponse;
import com.reservex.payment.entity.Payment;
import com.reservex.payment.entity.PaymentTransaction;
import com.reservex.payment.enums.PaymentStatus;
import com.reservex.payment.enums.TransactionStatus;
import com.reservex.payment.mapper.PaymentMapper;
import com.reservex.payment.repository.PaymentRepository;
import com.reservex.payment.repository.PaymentTransactionRepository;
import com.reservex.payment.service.PaymentService;
import com.reservex.notification.service.NotificationService;
import com.reservex.booking.entity.BookingSeat;
import com.reservex.booking.enums.BookingStatus;
import com.reservex.booking.repository.BookingSeatRepository;
import com.reservex.inventory.entity.SeatLock;
import com.reservex.inventory.entity.ShowSeat;
import com.reservex.inventory.enums.LockStatus;
import com.reservex.inventory.enums.SeatStatus;
import com.reservex.inventory.repository.SeatLockRepository;
import com.reservex.inventory.repository.ShowSeatRepository;
import com.reservex.inventory.service.SeatLockRedisService;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl
        implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;
    private final BookingSeatRepository bookingSeatRepository;

    private final SeatLockRepository seatLockRepository;
    private final ShowSeatRepository showSeatRepository;
    private final SeatLockRedisService seatLockRedisService;

    @Override
    @Transactional
    public PaymentResponse createPayment(
            UUID userId,
            CreatePaymentRequest request
    ) {

        UUID bookingId =
                UUID.fromString(
                        request.getBookingId()
                );

        Booking booking =
                bookingRepository.findById(
                        bookingId
                ).orElseThrow(() ->
                        new AppException(
                                HttpStatus.NOT_FOUND,
                                "BOOKING_NOT_FOUND",
                                "Booking not found"
                        )
                );

        if (!booking.getUserId().equals(userId)) {

            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "ACCESS_DENIED",
                    "Booking does not belong to user"
            );
        }

        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new AppException(
            HttpStatus.BAD_REQUEST,
            "BOOKING_ALREADY_CONFIRMED",
            "Booking has already been paid"
           );
        }
        paymentRepository
                .findByBooking_Id(
                        bookingId
                )
                .ifPresent(payment -> {
                    throw new AppException(
                            HttpStatus.CONFLICT,
                            "PAYMENT_ALREADY_EXISTS",
                            "Payment already exists for booking"
                    );
                });

        String txnRef =
                "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        Payment payment =
                paymentRepository.save(
                        Payment.builder()
                                .booking(booking)
                                .amount(
                                        booking.getTotalAmount()
                                )
                                .paymentMethod(
                                        request.getPaymentMethod()
                                )
                                .paymentStatus(
                                        PaymentStatus.SUCCESS
                                )
                                .transactionReference(
                                        txnRef
                                )
                                .build()
                );

        paymentTransactionRepository.save(
                PaymentTransaction.builder()
                        .payment(payment)
                        .gatewayReference(
                                "FAKE_GATEWAY"
                        )
                        .requestPayload(
                                request.toString()
                        )
                        .responsePayload(
                                "SUCCESS"
                        )
                        .transactionStatus(
                                TransactionStatus.SUCCESS
                        )
                        .build()
        );

        // ------------------------------------------------------------
        // Payment successful.
        // Confirm booking and permanently reserve seats.
        // ------------------------------------------------------------

        // Confirm booking
        booking.setBookingStatus(
        BookingStatus.CONFIRMED
        );
        bookingRepository.save(booking);   

        // Fetch booking seats
        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBooking(booking);

        // Mark seats as BOOKED
        for (BookingSeat bookingSeat : bookingSeats) {

                ShowSeat showSeat = bookingSeat.getShowSeat();

                showSeat.setStatus(
                SeatStatus.BOOKED
                );

                showSeatRepository.save(showSeat);
        }

        // Fetch active locks
        List<SeatLock> seatLocks =
                seatLockRepository
                        .findByUserIdAndShowSeat_Show_IdAndLockStatus(
                        userId,
                        booking.getShow().getId(),
                        LockStatus.ACTIVE
                );

        // Release DB lock + Redis lock
        for (SeatLock lock : seatLocks) {

                lock.setLockStatus(
                LockStatus.RELEASED
        );
        seatLockRepository.save(lock);

        seatLockRedisService.unlockSeat(
                lock.getShowSeat().getShow().getId(),
                lock.getShowSeat().getSeat().getId()
                );
        }

        notificationService.createNotification(
        userId,
        "Payment Successful",
        "Payment of ₹" +
        payment.getAmount() +
        " completed successfully."
);

        log.info(
                "Payment completed. paymentId={}",
                payment.getId()
        );

        return paymentMapper.toResponse(
                payment
        );
    }

    @Override
    @Transactional
    public void processWebhook(
            PaymentWebhookRequest request
    ) {

        log.info(
                "Webhook received. paymentId={}",
                request.getPaymentId()
        );
    }
}