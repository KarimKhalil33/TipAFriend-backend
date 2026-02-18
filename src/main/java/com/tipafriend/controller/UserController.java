package com.tipafriend.controller;

import com.tipafriend.dto.response.UserResponse;
import com.tipafriend.model.User;
import com.tipafriend.security.SecurityUser;
import com.tipafriend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        User user = userService.getById(principal.getId());
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPhotoUrl(),
                user.getBio()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPhotoUrl(),
                user.getBio()
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> search(@RequestParam String q) {
        List<UserResponse> result = userService.searchUsers(q)
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getPhotoUrl(),
                        user.getBio()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }
}
