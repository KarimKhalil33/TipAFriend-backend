package com.tipafriend.controller;

import com.tipafriend.dto.request.CreateReviewRequest;
import com.tipafriend.dto.response.IdResponse;
import com.tipafriend.model.Review;
import com.tipafriend.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> create(@RequestBody CreateReviewRequest request) {
        Review review = reviewService.createReview(
                request.taskAssignmentId(),
                request.reviewerId(),
                request.rating(),
                request.comment()
        );
        return ResponseEntity.ok(new IdResponse(review.getId()));
    }
}

