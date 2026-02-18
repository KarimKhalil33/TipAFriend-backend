package com.tipafriend.dto.request;

public record CreateReviewRequest(
        Long taskAssignmentId,
        Long reviewerId,
        Integer rating,
        String comment
) {
}

