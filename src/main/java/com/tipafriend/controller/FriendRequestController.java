package com.tipafriend.controller;

import com.tipafriend.dto.request.SendFriendRequestRequest;
import com.tipafriend.dto.response.FriendRequestResponse;
import com.tipafriend.model.FriendRequest;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.FriendRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<FriendRequestResponse> sendRequest(@Valid @RequestBody SendFriendRequestRequest request,
                                                            Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        FriendRequest created = friendRequestService.sendRequest(currentUserId, request.toUserId());
        return ResponseEntity.ok(toResponse(created));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<FriendRequestResponse> accept(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        FriendRequest updated = friendRequestService.acceptRequest(id, currentUserId);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PutMapping("/{id}/decline")
    public ResponseEntity<FriendRequestResponse> decline(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        FriendRequest updated = friendRequestService.declineRequest(id, currentUserId);
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<FriendRequestResponse>> incoming(Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        List<FriendRequestResponse> result = friendRequestService.getIncomingRequests(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<FriendRequestResponse>> outgoing(Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        List<FriendRequestResponse> result = friendRequestService.getOutgoingRequests(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
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
