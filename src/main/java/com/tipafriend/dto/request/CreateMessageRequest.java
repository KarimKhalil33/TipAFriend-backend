package com.tipafriend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(
        @NotNull Long conversationId,
        @NotBlank @Size(max = 4000) String body
) {
}

