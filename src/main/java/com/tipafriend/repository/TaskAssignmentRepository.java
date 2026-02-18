package com.tipafriend.repository;

import com.tipafriend.model.TaskAssignment;
import com.tipafriend.model.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    Optional<TaskAssignment> findByPostId(Long postId);
    List<TaskAssignment> findByAccepterId(Long accepterId);
    List<TaskAssignment> findByAccepterIdAndStatus(Long accepterId, PostStatus status);
}

