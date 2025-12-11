package com.estimate.api.controller;

import com.estimate.application.dto.UpdateUserRequest;
import com.estimate.application.dto.UserResponse;
import com.estimate.application.service.UserService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getId()));
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(principal.getId(), request));
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteUser(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
