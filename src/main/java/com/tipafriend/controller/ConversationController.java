package com.tipafriend.controller;

import com.tipafriend.dto.request.CreateConversationRequest;
import com.tipafriend.dto.request.CreateMessageRequest;
import com.tipafriend.dto.response.IdResponse;
import com.tipafriend.dto.response.MessageResponse;
import com.tipafriend.model.Conversation;
import com.tipafriend.model.Message;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> create(@Valid @RequestBody CreateConversationRequest request,
                                             Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        if (!request.participantIds().contains(currentUserId)) {
            throw new com.tipafriend.exception.BadRequestException("Creator must be a participant");
        }
        Conversation conversation = conversationService.createConversation(
                request.type(),
                request.taskAssignmentId(),
                request.participantIds()
        );
        return ResponseEntity.ok(new IdResponse(conversation.getId()));
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> messages(@PathVariable Long conversationId,
                                                          Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        List<MessageResponse> result = conversationService.getMessages(conversationId, currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/messages")
    public ResponseEntity<IdResponse> sendMessage(@Valid @RequestBody CreateMessageRequest request,
                                                  Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        Message message = conversationService.sendMessage(request.conversationId(), currentUserId, request.body());
        return ResponseEntity.ok(new IdResponse(message.getId()));
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getBody(),
                message.getCreatedAt()
        );
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
