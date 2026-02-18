package com.tipafriend.dto.response;

import com.tipafriend.model.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String body,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
}

