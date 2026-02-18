package com.tipafriend.dto.response;

import com.tipafriend.model.enums.FriendRequestStatus;

import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long id,
        Long fromUserId,
        Long toUserId,
        FriendRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime respondedAt
) {
}

