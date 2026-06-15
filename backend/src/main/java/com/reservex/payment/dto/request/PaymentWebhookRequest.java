package com.reservex.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentWebhookRequest {

    private String paymentId;

    private String status;
}