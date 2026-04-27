package com.tipafriend.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long taskAssignmentId,
        Long reviewerId,
        Long revieweeId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}

