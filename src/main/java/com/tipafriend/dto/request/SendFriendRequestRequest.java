package com.tipafriend.dto.request;

public record SendFriendRequestRequest(
        Long fromUserId,
        Long toUserId
) {
}

