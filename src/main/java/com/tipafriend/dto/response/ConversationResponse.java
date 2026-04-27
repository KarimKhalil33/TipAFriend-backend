package com.tipafriend.dto.response;

import com.tipafriend.model.enums.ConversationType;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationResponse(
        Long id,
        ConversationType type,
        Long taskAssignmentId,
        List<UserResponse> participants,
        MessageResponse lastMessage,
        Integer unreadCount,
        LocalDateTime createdAt
) {
}

