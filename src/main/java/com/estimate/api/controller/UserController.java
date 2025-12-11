package com.estimate.api.controller;

import com.estimate.application.dto.UpdateUserRequest;
import com.estimate.application.dto.UserResponse;
import com.estimate.application.service.UserService;
import com.estimate.application.service.SampleDataService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final SampleDataService sampleDataService;
    
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
    
    @PostMapping("/me/sample-data")
    public ResponseEntity<Map<String, String>> generateSampleData(@AuthenticationPrincipal UserPrincipal principal) {
        if (sampleDataService.hasSampleData(principal.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Sample data already exists"));
        }
        sampleDataService.generateSampleData(principal.getId());
        return ResponseEntity.ok(Map.of("message", "Sample data generated successfully"));
    }
    
    @DeleteMapping("/me/sample-data")
    public ResponseEntity<Void> deleteSampleData(@AuthenticationPrincipal UserPrincipal principal) {
        sampleDataService.deleteSampleData(principal.getId());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/me/has-sample-data")
    public ResponseEntity<Map<String, Boolean>> hasSampleData(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(Map.of("hasSampleData", sampleDataService.hasSampleData(principal.getId())));
    }
}
