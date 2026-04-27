package com.tipafriend.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long conversationId,
        Long senderId,
        String body,
        boolean system,
        boolean isSystem,
        Long taskAssignmentId,
        String taskTitle,
        LocalDateTime createdAt
) {
    // Backwards-compatible convenience constructor.
    public MessageResponse(Long id, Long conversationId, Long senderId, String body, LocalDateTime createdAt) {
        this(id, conversationId, senderId, body, false, false, null, null, createdAt);
    }
}
