package com.tipafriend.dto.response;

import com.tipafriend.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long postId,
        Long payerId,
        Long payeeId,
        BigDecimal amount,
        PaymentStatus status,
        String stripePaymentIntentId,
        String stripeClientSecret,  // For frontend confirmation
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime paidAt
) {
}

