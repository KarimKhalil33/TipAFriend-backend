package com.tipafriend.dto.request;

import com.tipafriend.model.enums.PaymentStatus;

public record UpdatePaymentStatusRequest(
        PaymentStatus status,
        String errorMessage
) {
}

