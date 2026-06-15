package com.reservex.payment.mapper;

import com.reservex.payment.dto.response.PaymentResponse;
import com.reservex.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(
            Payment payment
    ) {

        return PaymentResponse.builder()
                .paymentId(payment.getId().toString())
                .bookingId(payment.getBooking().getId().toString())
                .amount(payment.getAmount().toString())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .transactionReference(
                        payment.getTransactionReference()
                )
                .build();
    }
}