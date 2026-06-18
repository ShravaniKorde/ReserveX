package com.reservex.payment.dto.request;

import com.reservex.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Booking ID is required")
    private String bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}