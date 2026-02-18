package com.tipafriend.repository;

import com.tipafriend.model.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    List<ConversationParticipant> findByUserId(Long userId);
    List<ConversationParticipant> findByConversationId(Long conversationId);
}

