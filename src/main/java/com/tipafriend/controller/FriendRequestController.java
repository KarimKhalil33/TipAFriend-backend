package com.tipafriend.controller;

import com.tipafriend.dto.request.SendFriendRequestRequest;
import com.tipafriend.dto.response.FriendRequestResponse;
import com.tipafriend.model.FriendRequest;
import com.tipafriend.service.FriendRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends/requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping
    public ResponseEntity<FriendRequestResponse> sendRequest(@RequestBody SendFriendRequestRequest request) {
        FriendRequest created = friendRequestService.sendRequest(request.fromUserId(), request.toUserId());
        return ResponseEntity.ok(toResponse(created));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendRequestResponse> accept(@PathVariable Long id, @RequestParam Long currentUserId) {
        FriendRequest updated = friendRequestService.acceptRequest(id, currentUserId);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PutMapping("/{id}/decline")
    public ResponseEntity<FriendRequestResponse> decline(@PathVariable Long id, @RequestParam Long currentUserId) {
        FriendRequest updated = friendRequestService.declineRequest(id, currentUserId);
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<FriendRequestResponse>> incoming(@RequestParam Long userId) {
        List<FriendRequestResponse> result = friendRequestService.getIncomingRequests(userId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<FriendRequestResponse>> outgoing(@RequestParam Long userId) {
        List<FriendRequestResponse> result = friendRequestService.getOutgoingRequests(userId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    private FriendRequestResponse toResponse(FriendRequest request) {
        return new FriendRequestResponse(
                request.getId(),
                request.getFromUser().getId(),
                request.getToUser().getId(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getRespondedAt()
        );
    }
}

