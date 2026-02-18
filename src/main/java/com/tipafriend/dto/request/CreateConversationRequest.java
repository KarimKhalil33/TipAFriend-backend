package com.tipafriend.dto.request;

import com.tipafriend.model.enums.ConversationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateConversationRequest(
        @NotNull ConversationType type,
        Long taskAssignmentId,
        @NotNull List<Long> participantIds
) {
}

