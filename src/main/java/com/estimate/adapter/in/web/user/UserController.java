package com.estimate.adapter.in.web.user;

import com.estimate.adapter.in.web.user.dto.ChangePasswordRequest;
import com.estimate.adapter.in.web.user.dto.UserRequest;
import com.estimate.adapter.in.web.user.dto.UserResponse;
import com.estimate.domain.model.User;
import com.estimate.domain.port.in.user.*;
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
    
    private final FindUserUseCase findUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    
    @GetMapping("/me")
    public Mono<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return findUserUseCase.findById(principal.getId())
                .map(this::toResponse);
    }
    
    @PutMapping("/me")
    public Mono<UserResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserRequest request) {
        UpdateUserCommand command = UpdateUserCommand.builder()
                .userId(principal.getId())
                .companyName(request.getCompanyName())
                .phone(request.getPhone())
                .build();
        
        return updateUserUseCase.update(command)
                .map(this::toResponse);
    }
    
    @PostMapping("/me/change-password")
    public Mono<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        ChangePasswordCommand command = ChangePasswordCommand.builder()
                .userId(principal.getId())
                .oldPassword(request.getOldPassword())
                .newPassword(request.getNewPassword())
                .build();
        
        return changePasswordUseCase.changePassword(command);
    }
    
    @DeleteMapping("/me")
    public Mono<Void> deleteAccount(@AuthenticationPrincipal UserPrincipal principal) {
        return deleteUserUseCase.delete(principal.getId());
    }
    
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .companyName(user.getCompanyName())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
