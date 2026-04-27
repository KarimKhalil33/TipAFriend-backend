package com.tipafriend.repository;

import com.tipafriend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT DISTINCT c FROM Conversation c JOIN ConversationParticipant cp ON cp.conversation.id = c.id WHERE cp.user.id = :userId ORDER BY c.createdAt DESC")
    List<Conversation> findByParticipantUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT c FROM Conversation c WHERE c.id IN (SELECT cp.conversation.id FROM ConversationParticipant cp WHERE cp.user.id = :userId1) AND c.id IN (SELECT cp.conversation.id FROM ConversationParticipant cp WHERE cp.user.id = :userId2)")
    List<Conversation> findConversationBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

