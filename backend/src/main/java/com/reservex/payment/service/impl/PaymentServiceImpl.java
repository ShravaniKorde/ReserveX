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