package com.tipafriend.repository;

import com.tipafriend.model.Payment;
import com.tipafriend.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPostId(Long postId);
    List<Payment> findByPayerId(Long payerId);
    List<Payment> findByPayeeId(Long payeeId);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    @Query("SELECT p FROM Payment p WHERE p.post.id IN (SELECT ta.post.id FROM TaskAssignment ta WHERE ta.id = :taskAssignmentId) AND p.status = 'COMPLETED' ORDER BY p.createdAt DESC")
    Optional<Payment> findByTaskAssignmentId(@Param("taskAssignmentId") Long taskAssignmentId);
}

