package com.reservex.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private String paymentId;

    private String bookingId;

    private String amount;

    private String paymentMethod;

    private String paymentStatus;

    private String transactionReference;
}