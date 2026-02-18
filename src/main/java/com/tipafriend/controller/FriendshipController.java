package com.tipafriend.controller;

import com.tipafriend.service.FriendshipService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Long>> listFriends(@RequestParam Long userId) {
        return ResponseEntity.ok(friendshipService.getFriendIds(userId));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long friendId, @RequestParam Long userId) {
        friendshipService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}

