package com.tipafriend.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long conversationId,
        Long senderId,
        String body,
        LocalDateTime createdAt
) {
}

