package com.estimate.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    private String companyName;
    
    private String phone;
    
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;
    
    private String currentPassword;
}
