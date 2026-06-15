package com.reservex.payment.service;

import com.reservex.payment.dto.request.CreatePaymentRequest;
import com.reservex.payment.dto.request.PaymentWebhookRequest;
import com.reservex.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(
            UUID userId,
            CreatePaymentRequest request
    );

    void processWebhook(
            PaymentWebhookRequest request
    );
}