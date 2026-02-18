package com.tipafriend.service;

import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.model.Notification;
import com.tipafriend.model.User;
import com.tipafriend.model.enums.NotificationType;
import com.tipafriend.repository.NotificationRepository;
import com.tipafriend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification create(Long recipientId, NotificationType type, String title, String body) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + recipientId));
        Notification notification = new Notification(recipient, type, title, body);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification markRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found: " + notificationId);
        }

        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }
}

