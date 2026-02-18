package com.tipafriend.controller;

import com.tipafriend.dto.response.IdResponse;
import com.tipafriend.model.TaskAssignment;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.TaskAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskAssignmentController {

    private final TaskAssignmentService taskAssignmentService;

    public TaskAssignmentController(TaskAssignmentService taskAssignmentService) {
        this.taskAssignmentService = taskAssignmentService;
    }

    @PostMapping("/posts/{postId}/accept")
    public ResponseEntity<IdResponse> accept(@PathVariable Long postId, Authentication authentication) {
        Long userId = currentUserId(authentication);
        TaskAssignment task = taskAssignmentService.acceptPost(postId, userId);
        return ResponseEntity.ok(new IdResponse(task.getId()));
    }

    @PutMapping("/{taskId}/in-progress")
    public ResponseEntity<IdResponse> inProgress(@PathVariable Long taskId, Authentication authentication) {
        Long userId = currentUserId(authentication);
        TaskAssignment task = taskAssignmentService.markInProgress(taskId, userId);
        return ResponseEntity.ok(new IdResponse(task.getId()));
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<IdResponse> complete(@PathVariable Long taskId, Authentication authentication) {
        Long userId = currentUserId(authentication);
        TaskAssignment task = taskAssignmentService.completeTask(taskId, userId);
        return ResponseEntity.ok(new IdResponse(task.getId()));
    }

    private Long currentUserId(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return principal.getId();
    }
}
