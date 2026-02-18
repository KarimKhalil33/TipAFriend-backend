package com.tipafriend.dto.response;

public record UserResponse(
        Long id,
        String email,
        String username,
        String displayName,
        String photoUrl,
        String bio
) {
}

