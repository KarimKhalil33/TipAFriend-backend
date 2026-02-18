package com.tipafriend.controller;

import com.tipafriend.dto.request.CreatePaymentRequest;
import com.tipafriend.dto.request.UpdatePaymentStatusRequest;
import com.tipafriend.dto.response.IdResponse;
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
    public ResponseEntity<IdResponse> create(@Valid @RequestBody CreatePaymentRequest request,
                                             Authentication authentication) {
        Long payerId = currentUserId(authentication);
        Payment payment = paymentService.createPayment(
                request.postId(),
                payerId,
                request.payeeId(),
                request.amount(),
                request.stripePaymentIntentId()
        );
        return ResponseEntity.ok(new IdResponse(payment.getId()));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<IdResponse> updateStatus(@PathVariable Long paymentId,
                                                   @Valid @RequestBody UpdatePaymentStatusRequest request) {
        Payment payment = paymentService.updateStatus(paymentId, request.status(), request.errorMessage());
        return ResponseEntity.ok(new IdResponse(payment.getId()));
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
