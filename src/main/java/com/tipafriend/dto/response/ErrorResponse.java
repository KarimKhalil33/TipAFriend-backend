package com.tipafriend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,    // Stable error code like "DUPLICATE_REVIEW", "PAYMENT_NOT_FOUND"
        String message,      // Human-readable error message
        Object details       // Optional additional details
) {
}

