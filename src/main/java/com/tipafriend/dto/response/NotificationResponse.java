package com.tipafriend.dto.response;

import com.tipafriend.model.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String body,
        boolean read,
        boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    // Convenience constructor used by controllers — derives read flags from readAt.
    public NotificationResponse(
            Long id,
            NotificationType type,
            String title,
            String body,
            LocalDateTime readAt,
            LocalDateTime createdAt
    ) {
        this(id, type, title, body, readAt != null, readAt != null, readAt, createdAt);
    }
}
