package com.estimate.api.controller;

import com.estimate.application.dto.*;
import com.estimate.application.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final WorkService workService;
    private final RenovationTemplateService templateService;
    private final EstimateService estimateService;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUserAsAdmin(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/works")
    public ResponseEntity<List<WorkResponse>> getAllWorks() {
        return ResponseEntity.ok(workService.getAllWorks());
    }
    
    @GetMapping("/templates")
    public ResponseEntity<List<RenovationTemplateResponse>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }
    
    @GetMapping("/estimates")
    public ResponseEntity<List<EstimateResponse>> getAllEstimates() {
        return ResponseEntity.ok(estimateService.getAllEstimates());
    }
}
