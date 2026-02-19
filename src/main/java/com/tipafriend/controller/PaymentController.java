package com.tipafriend.controller;

import com.tipafriend.dto.request.CreatePaymentRequest;
import com.tipafriend.dto.request.UpdatePaymentStatusRequest;
import com.tipafriend.dto.response.PaymentResponse;
import com.tipafriend.model.Payment;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request,
                                             Authentication authentication) {
        Long payerId = currentUserId(authentication);
        Payment payment = paymentService.createPayment(
                request.postId(),
                payerId,
                request.payeeId(),
                request.amount(),
                request.stripePaymentIntentId()
        );
        return ResponseEntity.ok(toResponse(payment));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> updateStatus(@PathVariable Long paymentId,
                                                   @Valid @RequestBody UpdatePaymentStatusRequest request) {
        Payment payment = paymentService.updateStatus(paymentId, request.status(), request.errorMessage());
        return ResponseEntity.ok(toResponse(payment));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPost().getId(),
                payment.getPayer().getId(),
                payment.getPayee().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getStripePaymentIntentId(),
                payment.getStripeClientSecret(),
                payment.getErrorMessage(),
                payment.getCreatedAt(),
                payment.getPaidAt()
        );
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
