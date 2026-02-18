package com.tipafriend.repository;

import com.tipafriend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRevieweeId(Long revieweeId);
    Optional<Review> findByTaskAssignmentIdAndReviewerId(Long taskAssignmentId, Long reviewerId);
    List<Review> findByTaskAssignmentId(Long taskAssignmentId);
}

