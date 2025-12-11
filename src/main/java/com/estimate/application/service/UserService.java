package com.estimate.application.service;

import com.estimate.application.dto.UpdateUserRequest;
import com.estimate.application.dto.UserResponse;
import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return toResponse(user);
    }
    
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (request.getCompanyName() != null) {
            user.setCompanyName(request.getCompanyName());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null) {
                throw new IllegalArgumentException("Current password is required to change password");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            log.info("Password changed for user: {}", user.getEmail());
        }
        
        user = userRepository.save(user);
        log.info("User updated: {}", user.getEmail());
        
        return toResponse(user);
    }
    
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
        log.info("User deleted: {}", userId);
    }
    
    // Admin methods
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
    
    public void deleteUserAsAdmin(String userId) {
        userRepository.deleteById(userId);
        log.info("User deleted by admin: {}", userId);
    }
    
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .phone(user.getPhone())
                .build();
    }
}
