package com.tipafriend.repository;

import com.tipafriend.model.Payment;
import com.tipafriend.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPostId(Long postId);
    List<Payment> findByPayerId(Long payerId);
    List<Payment> findByPayeeId(Long payeeId);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}

