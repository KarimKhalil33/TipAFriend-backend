package com.tipafriend.controller;

import com.tipafriend.dto.response.NotificationResponse;
import com.tipafriend.model.Notification;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(Authentication authentication) {
        Long userId = currentUserId(authentication);
        List<NotificationResponse> result = notificationService.getNotifications(userId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id, Authentication authentication) {
        Long userId = currentUserId(authentication);
        Notification notification = notificationService.markRead(id, userId);
        return ResponseEntity.ok(toResponse(notification));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}

