package com.tipafriend.controller;

import com.tipafriend.dto.request.CreateReviewRequest;
import com.tipafriend.dto.response.IdResponse;
import com.tipafriend.model.Review;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> create(@Valid @RequestBody CreateReviewRequest request,
                                             Authentication authentication) {
        Long reviewerId = currentUserId(authentication);
        Review review = reviewService.createReview(
                request.taskAssignmentId(),
                reviewerId,
                request.rating(),
                request.comment()
        );
        return ResponseEntity.ok(new IdResponse(review.getId()));
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
