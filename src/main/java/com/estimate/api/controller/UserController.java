package com.estimate.api.controller;

import com.estimate.application.dto.UpdateUserRequest;
import com.estimate.application.dto.UserResponse;
import com.estimate.application.service.UserService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public Mono<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getCurrentUser(principal.getId());
    }
    
    @PutMapping("/me")
    public Mono<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(principal.getId(), request);
    }
    
    @DeleteMapping("/me")
    public Mono<Void> deleteUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.deleteUser(principal.getId());
    }
}
