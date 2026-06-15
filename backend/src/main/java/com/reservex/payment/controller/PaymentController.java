package com.reservex.payment.controller;

import com.reservex.common.response.ApiResponse;
import com.reservex.payment.dto.request.CreatePaymentRequest;
import com.reservex.payment.dto.request.PaymentWebhookRequest;
import com.reservex.payment.dto.response.PaymentResponse;
import com.reservex.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>>
    createPayment(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.createPayment(
                        UUID.fromString(userId),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(response)
                );
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void>
    webhook(
            @RequestBody PaymentWebhookRequest request
    ) {

        paymentService.processWebhook(
                request
        );

        return ResponseEntity.ok().build();
    }
}