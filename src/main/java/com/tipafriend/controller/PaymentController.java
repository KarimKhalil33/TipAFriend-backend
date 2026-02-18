package com.tipafriend.controller;

import com.tipafriend.dto.request.CreatePaymentRequest;
import com.tipafriend.dto.request.UpdatePaymentStatusRequest;
import com.tipafriend.dto.response.IdResponse;
import com.tipafriend.model.Payment;
import com.tipafriend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> create(@RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.createPayment(
                request.postId(),
                request.payerId(),
                request.payeeId(),
                request.amount(),
                request.stripePaymentIntentId()
        );
        return ResponseEntity.ok(new IdResponse(payment.getId()));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<IdResponse> updateStatus(@PathVariable Long paymentId,
                                                   @RequestBody UpdatePaymentStatusRequest request) {
        Payment payment = paymentService.updateStatus(paymentId, request.status(), request.errorMessage());
        return ResponseEntity.ok(new IdResponse(payment.getId()));
    }
}

