package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.exception.UnauthorizedException;
import com.tipafriend.model.Review;
import com.tipafriend.model.TaskAssignment;
import com.tipafriend.model.User;
import com.tipafriend.repository.ReviewRepository;
import com.tipafriend.repository.TaskAssignmentRepository;
import com.tipafriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         TaskAssignmentRepository taskAssignmentRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review createReview(Long taskAssignmentId, Long reviewerId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        TaskAssignment taskAssignment = taskAssignmentRepository.findById(taskAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskAssignmentId));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerId));

        if (reviewRepository.findByTaskAssignmentIdAndReviewerId(taskAssignmentId, reviewerId).isPresent()) {
            throw new BadRequestException("You already reviewed this task");
        }

        User author = taskAssignment.getPost().getAuthor();
        User accepter = taskAssignment.getAccepter();

        User reviewee;
        if (reviewer.getId().equals(author.getId())) {
            reviewee = accepter;
        } else if (reviewer.getId().equals(accepter.getId())) {
            reviewee = author;
        } else {
            throw new UnauthorizedException("Not authorized to review this task");
        }

        Review review = new Review(taskAssignment, reviewer, reviewee, rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }
}

