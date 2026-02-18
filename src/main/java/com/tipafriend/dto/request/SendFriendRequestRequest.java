package com.tipafriend.dto.request;

import jakarta.validation.constraints.NotNull;

public record SendFriendRequestRequest(
        @NotNull Long toUserId
) {
}
