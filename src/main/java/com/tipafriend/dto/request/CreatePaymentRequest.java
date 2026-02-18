package com.tipafriend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull Long postId,
        @NotNull Long payeeId,
        @NotNull @Positive BigDecimal amount,
        String stripePaymentIntentId
) {
}
