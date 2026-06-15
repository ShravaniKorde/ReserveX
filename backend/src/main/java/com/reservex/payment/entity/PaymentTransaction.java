package com.reservex.payment.entity;

import com.reservex.payment.enums.TransactionStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "payment_id",
            nullable = false
    )
    private Payment payment;

    @Column(
            name = "gateway_reference",
            nullable = false
    )
    private String gatewayReference;

    @Column(
            name = "request_payload",
            columnDefinition = "TEXT"
    )
    private String requestPayload;

    @Column(
            name = "response_payload",
            columnDefinition = "TEXT"
    )
    private String responsePayload;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "transaction_status",
            nullable = false
    )
    private TransactionStatus transactionStatus;

    @CreationTimestamp
    @Column(
            name = "created_at",
            updatable = false
    )
    private Instant createdAt;
}