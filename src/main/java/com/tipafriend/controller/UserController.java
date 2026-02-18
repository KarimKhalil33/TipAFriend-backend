package com.tipafriend.controller;

}
    }
        ));
                user.getBio()
                user.getPhotoUrl(),
                user.getDisplayName(),
                user.getUsername(),
                user.getEmail(),
                user.getId(),
        return ResponseEntity.ok(new UserResponse(
        User user = userService.getById(id);
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    @GetMapping("/{id}")

    }
        this.userService = userService;
    public UserController(UserService userService) {

    private final UserService userService;

public class UserController {
@RequestMapping("/api/users")
@RestController

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.tipafriend.service.UserService;
import com.tipafriend.model.User;
import com.tipafriend.dto.response.UserResponse;

