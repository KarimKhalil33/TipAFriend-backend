package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.model.Payment;
import com.tipafriend.model.Post;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.PaymentStatus;
import com.tipafriend.model.enums.NotificationType;
import com.tipafriend.repository.PaymentRepository;
import com.tipafriend.repository.PostRepository;
import com.tipafriend.repository.TaskAssignmentRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ConversationService conversationService;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final String stripeSecretKey;
    private final String stripeWebhookSecret;

    public PaymentService(PaymentRepository paymentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          ConversationService conversationService,
                          TaskAssignmentRepository taskAssignmentRepository,
                          @Value("${stripe.secret-key:}") String stripeSecretKey,
                          @Value("${stripe.webhook-secret:}") String stripeWebhookSecret) {
        this.paymentRepository = paymentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.conversationService = conversationService;
        this.taskAssignmentRepository = taskAssignmentRepository;
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
            logger.info("🔵 PAYMENT: Creating PaymentIntent for post={}, amount={}", postId, amount);
            Stripe.apiKey = stripeSecretKey;
            try {
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount(amount.multiply(new BigDecimal("100")).longValue())
                        .setCurrency("usd")
                        .putMetadata("postId", postId.toString())
                        .putMetadata("payerId", payerId.toString())
                        .putMetadata("payeeId", payeeId.toString())
                        .build();

                logger.info("🔵 PAYMENT: Calling Stripe API...");
                PaymentIntent intent = PaymentIntent.create(params);

                logger.info("✅ PAYMENT: PaymentIntent created: {}", intent.getId());
                logger.info("✅ PAYMENT: Client Secret obtained: {}", intent.getClientSecret() != null ? "YES" : "NO");

                payment.setStripePaymentIntentId(intent.getId());
                payment.setStripeClientSecret(intent.getClientSecret());
                payment.setStatus(PaymentStatus.PROCESSING);

            } catch (StripeException e) {
                logger.error("❌ PAYMENT: Stripe API Error - Code: {}, Message: {}, Type: {}",
                    e.getCode(), e.getMessage(), e.getClass().getSimpleName());
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorMessage(e.getMessage());
            } catch (Exception e) {
                logger.error("❌ PAYMENT: Unexpected error creating PaymentIntent", e);
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorMessage("Internal error: " + e.getMessage());
            }
        } else {
            logger.warn("⚠️  PAYMENT: Stripe secret key not configured, creating PENDING payment");
            payment.setStripePaymentIntentId(stripePaymentIntentId);
            payment.setStatus(PaymentStatus.PENDING);
        }

        Payment saved = paymentRepository.save(payment);
        logger.info("💾 PAYMENT: Payment saved with status={}, hasClientSecret={}",
            saved.getStatus(), saved.getStripeClientSecret() != null ? "YES" : "NO");
        return saved;
    }

    @Transactional
    public Payment updateStatus(Long paymentId, PaymentStatus status, String errorMessage) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        payment.setStatus(status);
        payment.setErrorMessage(errorMessage);
        return paymentRepository.save(payment);
    }

    public Payment getByTaskAssignmentId(Long taskAssignmentId) {
        return paymentRepository.findByTaskAssignmentId(taskAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for task: " + taskAssignmentId));
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

                // Notify payee
                notificationService.create(
                    payment.getPayee().getId(),
                    NotificationType.PAYMENT_SUCCEEDED,
                    "Payment Received",
                    "You received $" + payment.getAmount() + " for: " + payment.getPost().getTitle()
                );

                // Notify payer
                notificationService.create(
                    payment.getPayer().getId(),
                    NotificationType.PAYMENT_SUCCEEDED,
                    "Payment Sent",
                    "Payment of $" + payment.getAmount() + " was successful for: " + payment.getPost().getTitle()
                );

                // Post system message into the DIRECT conversation between payer and payee
                taskAssignmentRepository.findAllByPostIdOrderByAcceptedAtDesc(payment.getPost().getId())
                        .stream().findFirst()
                        .ifPresent(task -> conversationService.postSystemMessage(
                                payment.getPayer().getId(),
                                payment.getPayee().getId(),
                                task,
                                "Payment of $" + payment.getAmount() + " sent for: " + payment.getPost().getTitle(),
                                payment.getPayer().getId()
                        ));
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
