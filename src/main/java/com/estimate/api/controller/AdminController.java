package com.estimate.api.controller;

import com.estimate.application.dto.*;
import com.estimate.application.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @DeleteMapping("/users/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUserAsAdmin(id);
    }
    
    @GetMapping("/works")
    public Flux<WorkResponse> getAllWorks() {
        return workService.getAllWorks();
    }
    
    @GetMapping("/templates")
    public Flux<RenovationTemplateResponse> getAllTemplates() {
        return templateService.getAllTemplates();
    }
    
    @GetMapping("/estimates")
    public Flux<EstimateResponse> getAllEstimates() {
        return estimateService.getAllEstimates();
    }
}
