package com.tipafriend.service;

import com.tipafriend.dto.request.LoginRequest;
import com.tipafriend.dto.request.RegisterRequest;
import com.tipafriend.dto.response.AuthResponse;
import com.tipafriend.dto.response.UserResponse;
import com.tipafriend.exception.BadRequestException;
import com.tipafriend.exception.ResourceNotFoundException;
import com.tipafriend.model.User;
import com.tipafriend.repository.UserRepository;
import com.tipafriend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already in use");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponse(token, toUserResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.username());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(request.username());
        }

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, toUserResponse(user));
    }

    public UserResponse me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPhotoUrl(),
                user.getBio()
        );
    }
}

