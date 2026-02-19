package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.exception.UnauthorizedException;
import com.tipafriend.model.*;
import com.tipafriend.model.enums.ConversationType;
import com.tipafriend.model.enums.NotificationType;
import com.tipafriend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final NotificationService notificationService;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationParticipantRepository participantRepository,
                               MessageRepository messageRepository,
                               UserRepository userRepository,
                               TaskAssignmentRepository taskAssignmentRepository,
                               NotificationService notificationService) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Conversation createConversation(Long currentUserId, ConversationType type, Long taskAssignmentId, List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new BadRequestException("Participants required");
        }

        // For DIRECT conversations, auto-add current user if not included
        if (type == ConversationType.DIRECT) {
            if (!participantIds.contains(currentUserId)) {
                participantIds.add(currentUserId);
            }

            // Check if conversation already exists between these two users
            if (participantIds.size() == 2) {
                Conversation existing = findExistingDirectConversation(participantIds.get(0), participantIds.get(1));
                if (existing != null) {
                    return existing;
                }
            }
        }

        TaskAssignment task = null;
        if (type == ConversationType.TASK_THREAD) {
            if (taskAssignmentId == null) {
                throw new BadRequestException("Task assignment required for task thread");
            }
            task = taskAssignmentRepository.findById(taskAssignmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskAssignmentId));

            Long authorId = task.getPost().getAuthor().getId();
            Long accepterId = task.getAccepter().getId();
            if (!participantIds.contains(authorId) || !participantIds.contains(accepterId)) {
                throw new BadRequestException("Task thread must include author and accepter");
            }
        }

        Conversation conversation = new Conversation(type, task);
        Conversation saved = conversationRepository.save(conversation);

        for (Long userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            participantRepository.save(new ConversationParticipant(saved, user));
        }

        return saved;
    }

    private Conversation findExistingDirectConversation(Long userId1, Long userId2) {
        List<Conversation> user1Conversations = conversationRepository.findByParticipantUserId(userId1);
        List<Conversation> user2Conversations = conversationRepository.findByParticipantUserId(userId2);

        for (Conversation conv : user1Conversations) {
            if (conv.getType() == ConversationType.DIRECT && user2Conversations.contains(conv)) {
                List<ConversationParticipant> participants = participantRepository.findByConversationId(conv.getId());
                if (participants.size() == 2) {
                    return conv;
                }
            }
        }
        return null;
    }

    public List<Message> getMessages(Long conversationId, Long currentUserId) {
        if (!isParticipant(conversationId, currentUserId)) {
            throw new UnauthorizedException("Not a participant in this conversation");
        }
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public Message sendMessage(Long conversationId, Long senderId, String body) {
        if (!isParticipant(conversationId, senderId)) {
            throw new UnauthorizedException("Not a participant in this conversation");
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + conversationId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + senderId));

        Message message = new Message(conversation, sender, body);
        Message saved = messageRepository.save(message);

        // Notify all participants except sender
        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversationId);
        for (ConversationParticipant participant : participants) {
            if (!participant.getUser().getId().equals(senderId)) {
                String preview = body.length() > 50 ? body.substring(0, 50) + "..." : body;
                notificationService.create(
                    participant.getUser().getId(),
                    NotificationType.MESSAGE_RECEIVED,
                    "New Message",
                    sender.getDisplayName() + ": " + preview
                );
            }
        }

        return saved;
    }

    private boolean isParticipant(Long conversationId, Long userId) {
        return participantRepository.findByConversationId(conversationId)
                .stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
    }
}
