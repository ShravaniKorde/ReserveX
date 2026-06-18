package com.reservex.payment.repository;

import com.reservex.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, UUID> {

    List<PaymentTransaction> findByPayment_Id(
            UUID paymentId
    );
}