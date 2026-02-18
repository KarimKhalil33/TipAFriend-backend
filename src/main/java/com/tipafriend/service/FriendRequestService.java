package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.exception.UnauthorizedException;
import com.tipafriend.model.FriendRequest;
import com.tipafriend.model.Friendship;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.FriendRequestStatus;
import com.tipafriend.repository.FriendRequestRepository;
import com.tipafriend.repository.FriendshipRepository;
import com.tipafriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository,
                                FriendshipRepository friendshipRepository,
                                UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FriendRequest sendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new BadRequestException("Cannot send friend request to yourself");
        }

        if (friendshipRepository.existsByUserIdAndFriendId(fromUserId, toUserId)) {
            throw new BadRequestException("Already friends");
        }

        friendRequestRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .ifPresent(req -> {
                    throw new BadRequestException("Friend request already exists");
                });

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + fromUserId));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + toUserId));

        FriendRequest request = new FriendRequest(fromUser, toUser);
        return friendRequestRepository.save(request);
    }

    @Transactional
    public FriendRequest acceptRequest(Long requestId, Long currentUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found: " + requestId));

        if (!request.getToUser().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized to accept this request");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new BadRequestException("Friend request already processed");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());

        Friendship friendshipA = new Friendship(request.getFromUser(), request.getToUser());
        Friendship friendshipB = new Friendship(request.getToUser(), request.getFromUser());

        friendshipRepository.save(friendshipA);
        friendshipRepository.save(friendshipB);

        return friendRequestRepository.save(request);
    }

    @Transactional
    public FriendRequest declineRequest(Long requestId, Long currentUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found: " + requestId));

        if (!request.getToUser().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized to decline this request");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new BadRequestException("Friend request already processed");
        }

        request.setStatus(FriendRequestStatus.DECLINED);
        request.setRespondedAt(LocalDateTime.now());
        return friendRequestRepository.save(request);
    }

    public List<FriendRequest> getIncomingRequests(Long userId) {
        return friendRequestRepository.findByToUserIdAndStatus(userId, FriendRequestStatus.PENDING);
    }

    public List<FriendRequest> getOutgoingRequests(Long userId) {
        return friendRequestRepository.findByFromUserIdAndStatus(userId, FriendRequestStatus.PENDING);
    }
}
