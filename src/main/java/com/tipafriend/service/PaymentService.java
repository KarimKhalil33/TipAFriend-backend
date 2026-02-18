package com.tipafriend.service;

import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.model.Payment;
import com.tipafriend.model.Post;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.PaymentStatus;
import com.tipafriend.repository.PaymentRepository;
import com.tipafriend.repository.PostRepository;
import com.tipafriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Payment createPayment(Long postId, Long payerId, Long payeeId, BigDecimal amount, String stripePaymentIntentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
        User payer = userRepository.findById(payerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + payerId));
        User payee = userRepository.findById(payeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + payeeId));

        Payment payment = new Payment(post, payer, payee, amount);
        payment.setStripePaymentIntentId(stripePaymentIntentId);
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updateStatus(Long paymentId, PaymentStatus status, String errorMessage) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        payment.setStatus(status);
        payment.setErrorMessage(errorMessage);
        return paymentRepository.save(payment);
    }
}

