package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.exception.UnauthorizedException;
import com.tipafriend.model.Post;
import com.tipafriend.model.TaskAssignment;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.PostStatus;
import com.tipafriend.repository.PostRepository;
import com.tipafriend.repository.TaskAssignmentRepository;
import com.tipafriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final PostRepository postRepository;
    private final FriendshipService friendshipService;
    private final UserRepository userRepository;

    public TaskAssignmentService(TaskAssignmentRepository taskAssignmentRepository,
                                 PostRepository postRepository,
                                 FriendshipService friendshipService,
                                 UserRepository userRepository) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.postRepository = postRepository;
        this.friendshipService = friendshipService;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskAssignment acceptPost(Long postId, Long accepterId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        if (post.getStatus() != PostStatus.OPEN) {
            throw new BadRequestException("Post is not open for acceptance");
        }

        Long authorId = post.getAuthor().getId();
        if (authorId.equals(accepterId)) {
            throw new BadRequestException("Cannot accept your own post");
        }

        if (!friendshipService.areFriends(authorId, accepterId)) {
            throw new UnauthorizedException("Only friends can accept posts");
        }

        User accepter = userRepository.findById(accepterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + accepterId));

        TaskAssignment assignment = new TaskAssignment(post, accepter);
        assignment.setStatus(PostStatus.ACCEPTED);

        post.setStatus(PostStatus.ACCEPTED);
        postRepository.save(post);

        return taskAssignmentRepository.save(assignment);
    }

    @Transactional
    public TaskAssignment markInProgress(Long taskId, Long currentUserId) {
        TaskAssignment task = taskAssignmentRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (!task.getAccepter().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Only accepter can mark in progress");
        }

        task.setStatus(PostStatus.IN_PROGRESS);
        task.getPost().setStatus(PostStatus.IN_PROGRESS);
        return taskAssignmentRepository.save(task);
    }

    @Transactional
    public TaskAssignment completeTask(Long taskId, Long currentUserId) {
        TaskAssignment task = taskAssignmentRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (!task.getAccepter().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Only accepter can mark completed");
        }

        task.setStatus(PostStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.getPost().setStatus(PostStatus.COMPLETED);
        return taskAssignmentRepository.save(task);
    }
}
