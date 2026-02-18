package com.tipafriend.service;

import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.model.Friendship;
import com.tipafriend.model.User;
import com.tipafriend.repository.FriendshipRepository;
import com.tipafriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public List<Long> getFriendIds(Long userId) {
        return friendshipRepository.findByUserId(userId)
                .stream()
                .map(friendship -> friendship.getFriend().getId())
                .collect(Collectors.toList());
    }

    public boolean areFriends(Long userId, Long friendId) {
        return friendshipRepository.existsByUserIdAndFriendId(userId, friendId);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        friendshipRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresent(friendshipRepository::delete);
        friendshipRepository.findByUserIdAndFriendId(friendId, userId)
                .ifPresent(friendshipRepository::delete);
    }

    @Transactional
    public void createFriendship(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException("Cannot befriend yourself");
        }

        if (friendshipRepository.existsByUserIdAndFriendId(userId, friendId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + friendId));

        friendshipRepository.save(new Friendship(user, friend));
        friendshipRepository.save(new Friendship(friend, user));
    }
}
