package com.tipafriend.dto.request;

import com.tipafriend.model.enums.ConversationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GetOrCreateConversationRequest(
        @NotNull(message = "Type is required") ConversationType type,
        Long taskAssignmentId,  // Required for TASK_THREAD
        @NotNull(message = "Participant IDs are required") List<Long> participantIds
) {
}

