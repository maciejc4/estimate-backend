package com.estimate.application.service;

import com.estimate.application.dto.UpdateUserRequest;
import com.estimate.application.dto.UserResponse;
import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Mono<UserResponse> getCurrentUser(String userId) {
        return userRepository.findById(userId)
                .map(this::toResponse)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }
    
    public Mono<UserResponse> updateUser(String userId, UpdateUserRequest request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> updateUserFields(user, request))
                .doOnNext(user -> log.info("User updated: {}", user.getEmail()))
                .map(this::toResponse);
    }
    
    public Mono<Void> deleteUser(String userId) {
        return userRepository.deleteById(userId)
                .doOnSuccess(v -> log.info("User deleted: {}", userId));
    }
    
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .map(this::toResponse);
    }
    
    public Mono<Void> deleteUserAsAdmin(String userId) {
        return userRepository.deleteById(userId)
                .doOnSuccess(v -> log.info("User deleted by admin: {}", userId));
    }
    
    private Mono<User> updateUserFields(User user, UpdateUserRequest request) {
        if (request.getCompanyName() != null) {
            user.setCompanyName(request.getCompanyName());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        if (request.getNewPassword() != null) {
            return updatePassword(user, request);
        }
        
        return userRepository.save(user);
    }
    
    private Mono<User> updatePassword(User user, UpdateUserRequest request) {
        if (request.getCurrentPassword() == null) {
            return Mono.error(new IllegalArgumentException("Current password is required to change password"));
        }
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return Mono.error(new IllegalArgumentException("Current password is incorrect"));
        }
        
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        log.info("Password changed for user: {}", user.getEmail());
        
        return userRepository.save(user);
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
