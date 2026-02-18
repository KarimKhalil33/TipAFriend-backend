package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.exception.UnauthorizedException;
import com.tipafriend.model.*;
import com.tipafriend.model.enums.ConversationType;
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

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationParticipantRepository participantRepository,
                               MessageRepository messageRepository,
                               UserRepository userRepository,
                               TaskAssignmentRepository taskAssignmentRepository) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    @Transactional
    public Conversation createConversation(ConversationType type, Long taskAssignmentId, List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            throw new BadRequestException("Participants required");
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
        return messageRepository.save(message);
    }

    private boolean isParticipant(Long conversationId, Long userId) {
        return participantRepository.findByConversationId(conversationId)
                .stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
    }
}
