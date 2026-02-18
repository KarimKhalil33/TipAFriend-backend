package com.tipafriend.repository;

import com.tipafriend.model.FriendRequest;
import com.tipafriend.model.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
    List<FriendRequest> findByToUserIdAndStatus(Long toUserId, FriendRequestStatus status);
    List<FriendRequest> findByFromUserIdAndStatus(Long fromUserId, FriendRequestStatus status);
}

