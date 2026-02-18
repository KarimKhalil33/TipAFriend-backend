package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.exception.UnauthorizedException;
import com.tipafriend.model.Post;
import com.tipafriend.model.TaskAssignment;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.PostCategory;
import com.tipafriend.model.enums.PostStatus;
import com.tipafriend.model.enums.PostType;
import com.tipafriend.repository.PostRepository;
import com.tipafriend.repository.UserRepository;
import com.tipafriend.repository.TaskAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       FriendshipService friendshipService,
                       TaskAssignmentRepository taskAssignmentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.friendshipService = friendshipService;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    @Transactional
    public Post createPost(Long authorId, Post post) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorId));
        post.setAuthor(author);
        post.setStatus(PostStatus.OPEN);
        return postRepository.save(post);
    }

    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
    }

    public Page<Post> getFriendsFeed(Long userId, PostType type, PostCategory category, Pageable pageable) {
        List<Long> friendIds = friendshipService.getFriendIds(userId);
        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }

        if (type != null && category != null) {
            return postRepository.findByAuthorIdInAndTypeAndCategoryAndStatus(friendIds, type, category, PostStatus.OPEN, pageable);
        }
        if (type != null) {
            return postRepository.findByAuthorIdInAndTypeAndStatus(friendIds, type, PostStatus.OPEN, pageable);
        }
        if (category != null) {
            return postRepository.findByAuthorIdInAndCategoryAndStatus(friendIds, category, PostStatus.OPEN, pageable);
        }
        return postRepository.findByAuthorIdInAndStatus(friendIds, PostStatus.OPEN, pageable);
    }

    @Transactional
    public Post updatePost(Long postId, Post updatedPost) {
        Post post = getById(postId);
        if (post.getStatus() != PostStatus.OPEN) {
            throw new BadRequestException("Cannot edit a post that is not OPEN");
        }

        post.setTitle(updatedPost.getTitle());
        post.setDescription(updatedPost.getDescription());
        post.setCategory(updatedPost.getCategory());
        post.setLocationName(updatedPost.getLocationName());
        post.setLatitude(updatedPost.getLatitude());
        post.setLongitude(updatedPost.getLongitude());
        post.setScheduledTime(updatedPost.getScheduledTime());
        post.setDurationMinutes(updatedPost.getDurationMinutes());
        post.setPaymentType(updatedPost.getPaymentType());
        post.setPrice(updatedPost.getPrice());

        return postRepository.save(post);
    }

    public List<Post> getMyPosts(Long userId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
    }

    public List<Post> getUserPosts(Long requesterId, Long targetUserId) {
        if (!requesterId.equals(targetUserId) && !friendshipService.areFriends(requesterId, targetUserId)) {
            throw new UnauthorizedException("Only friends can view posts");
        }
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(targetUserId);
    }

    public List<Post> getAcceptedPosts(Long accepterId) {
        return taskAssignmentRepository.findByAccepterId(accepterId)
                .stream()
                .map(TaskAssignment::getPost)
                .toList();
    }
}
