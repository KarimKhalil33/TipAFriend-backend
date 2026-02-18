package com.tipafriend.controller;

import com.tipafriend.dto.request.CreatePostRequest;
import com.tipafriend.dto.request.UpdatePostRequest;
import com.tipafriend.dto.response.PostResponse;
import com.tipafriend.model.Post;
import com.tipafriend.model.enums.PostCategory;
import com.tipafriend.model.enums.PostType;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(Authentication authentication,
                                                   @Valid @RequestBody CreatePostRequest request) {
        Long authorId = currentUserId(authentication);
        Post post = new Post();
        post.setType(request.type());
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setCategory(request.category());
        post.setLocationName(request.locationName());
        post.setLatitude(request.latitude());
        post.setLongitude(request.longitude());
        post.setScheduledTime(request.scheduledTime());
        post.setDurationMinutes(request.durationMinutes());
        post.setPaymentType(request.paymentType());
        post.setPrice(request.price());

        Post created = postService.createPost(authorId, post);
        return ResponseEntity.ok(toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(postService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
                                                   @Valid @RequestBody UpdatePostRequest request) {
        Post updated = new Post();
        updated.setTitle(request.title());
        updated.setDescription(request.description());
        updated.setCategory(request.category());
        updated.setLocationName(request.locationName());
        updated.setLatitude(request.latitude());
        updated.setLongitude(request.longitude());
        updated.setScheduledTime(request.scheduledTime());
        updated.setDurationMinutes(request.durationMinutes());
        updated.setPaymentType(request.paymentType());
        updated.setPrice(request.price());

        return ResponseEntity.ok(toResponse(postService.updatePost(id, updated)));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getFeed(Authentication authentication,
                                                      @RequestParam(required = false) PostType type,
                                                      @RequestParam(required = false) PostCategory category,
                                                      Pageable pageable) {
        Long userId = currentUserId(authentication);
        Page<PostResponse> result = postService.getFriendsFeed(userId, type, category, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(result);
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getType(),
                post.getTitle(),
                post.getDescription(),
                post.getCategory(),
                post.getLocationName(),
                post.getLatitude(),
                post.getLongitude(),
                post.getScheduledTime(),
                post.getDurationMinutes(),
                post.getPaymentType(),
                post.getPrice(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
