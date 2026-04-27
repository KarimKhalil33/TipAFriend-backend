package com.tipafriend.controller;

import com.tipafriend.dto.request.CreateConversationRequest;
import com.tipafriend.dto.request.CreateMessageRequest;
import com.tipafriend.dto.request.GetOrCreateConversationRequest;
import com.tipafriend.dto.response.ConversationResponse;
import com.tipafriend.dto.response.IdResponse;
import com.tipafriend.dto.response.MessageResponse;
import com.tipafriend.dto.response.UserResponse;
import com.tipafriend.model.Conversation;
import com.tipafriend.model.ConversationParticipant;
import com.tipafriend.model.Message;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.ConversationService;
import com.tipafriend.repository.ConversationParticipantRepository;
import com.tipafriend.repository.MessageRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;

    public ConversationController(ConversationService conversationService,
                                 ConversationParticipantRepository participantRepository,
                                 MessageRepository messageRepository) {
        this.conversationService = conversationService;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping
    public ResponseEntity<IdResponse> create(@Valid @RequestBody CreateConversationRequest request,
                                             Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        Conversation conversation = conversationService.createConversation(
                currentUserId,
                request.type(),
                request.taskAssignmentId(),
                request.participantIds()
        );
        return ResponseEntity.ok(new IdResponse(conversation.getId()));
    }

    @PostMapping("/get-or-create")
    public ResponseEntity<ConversationResponse> getOrCreate(@Valid @RequestBody GetOrCreateConversationRequest request,
                                                            Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        Conversation conversation = conversationService.getOrCreateConversation(
                currentUserId,
                request.type(),
                request.taskAssignmentId(),
                request.participantIds()
        );
        return ResponseEntity.ok(toConversationResponse(conversation));
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> list(Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        List<ConversationResponse> conversations = conversationService.listConversations(currentUserId)
                .stream()
                .map(this::toConversationResponse)
                .toList();
        return ResponseEntity.ok(conversations);
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

    private ConversationResponse toConversationResponse(Conversation conversation) {
        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversation.getId());
        List<UserResponse> participantResponses = participants.stream()
                .map(p -> new UserResponse(
                        p.getUser().getId(),
                        p.getUser().getEmail(),
                        p.getUser().getUsername(),
                        p.getUser().getDisplayName(),
                        p.getUser().getPhotoUrl(),
                        p.getUser().getBio()
                ))
                .toList();

        Message lastMessage = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId())
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);

        MessageResponse lastMessageResponse = lastMessage != null ? toResponse(lastMessage) : null;

        return new ConversationResponse(
                conversation.getId(),
                conversation.getType(),
                conversation.getTaskAssignment() != null ? conversation.getTaskAssignment().getId() : null,
                participantResponses,
                lastMessageResponse,
                0,  // TODO: Calculate unread count
                conversation.getCreatedAt()
        );
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender() != null ? message.getSender().getId() : null,
                message.getBody(),
                message.isSystem(),
                message.isSystem(),
                message.getTaskAssignment() != null ? message.getTaskAssignment().getId() : null,
                message.getTaskAssignment() != null && message.getTaskAssignment().getPost() != null
                        ? message.getTaskAssignment().getPost().getTitle()
                        : null,
                message.getCreatedAt()
        );
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
