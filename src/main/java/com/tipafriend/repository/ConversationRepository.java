package com.tipafriend.repository;

import com.tipafriend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT DISTINCT c FROM Conversation c JOIN ConversationParticipant cp ON cp.conversation.id = c.id WHERE cp.user.id = :userId")
    List<Conversation> findByParticipantUserId(@Param("userId") Long userId);
}

