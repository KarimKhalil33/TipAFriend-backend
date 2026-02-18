package com.tipafriend.controller;

import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping
    public ResponseEntity<List<Long>> listFriends(Authentication authentication) {
        Long userId = currentUserId(authentication);
        return ResponseEntity.ok(friendshipService.getFriendIds(userId));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long friendId, Authentication authentication) {
        Long userId = currentUserId(authentication);
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
