package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.model.Payment;
import com.tipafriend.model.Post;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.PaymentStatus;
import com.tipafriend.repository.PaymentRepository;
import com.tipafriend.repository.PostRepository;
import com.tipafriend.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final String stripeSecretKey;
    private final String stripeWebhookSecret;

    public PaymentService(PaymentRepository paymentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          @Value("${stripe.secret-key:}") String stripeSecretKey,
                          @Value("${stripe.webhook-secret:}") String stripeWebhookSecret) {
        this.paymentRepository = paymentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.stripeSecretKey = stripeSecretKey;
        this.stripeWebhookSecret = stripeWebhookSecret;
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

        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey;
            try {
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amount.multiply(new BigDecimal("100")).longValue())
                        .setCurrency("usd")
                        .putMetadata("postId", postId.toString())
                        .putMetadata("payerId", payerId.toString())
                        .putMetadata("payeeId", payeeId.toString())
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);
                payment.setStripePaymentIntentId(intent.getId());
                payment.setStatus(PaymentStatus.PROCESSING);
            } catch (StripeException e) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorMessage(e.getMessage());
            }
        } else {
            payment.setStripePaymentIntentId(stripePaymentIntentId);
            payment.setStatus(PaymentStatus.PENDING);
        }

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

    @Transactional
    public void handleStripeWebhook(String payload, String signature) {
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new BadRequestException("Stripe webhook secret not configured");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Invalid Stripe signature");
        }

        String type = event.getType();
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject().orElse(null);

        if (!(stripeObject instanceof PaymentIntent)) {
            return;
        }

        PaymentIntent intent = (PaymentIntent) stripeObject;
        String intentId = intent.getId();

        paymentRepository.findByStripePaymentIntentId(intentId).ifPresent(payment -> {
            if ("payment_intent.succeeded".equals(type)) {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                payment.setPaidAt(LocalDateTime.now());
                payment.setErrorMessage(null);
            } else if ("payment_intent.payment_failed".equals(type)) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorMessage("Payment failed");
            } else if ("payment_intent.processing".equals(type)) {
                payment.setStatus(PaymentStatus.PROCESSING);
            }
            paymentRepository.save(payment);
        });
    }
}
