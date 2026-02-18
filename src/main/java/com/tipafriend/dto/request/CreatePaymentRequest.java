package com.tipafriend.dto.request;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        Long postId,
        Long payerId,
        Long payeeId,
        BigDecimal amount,
        String stripePaymentIntentId
) {
}

