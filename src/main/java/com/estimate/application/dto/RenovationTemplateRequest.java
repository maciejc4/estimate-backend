package com.estimate.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenovationTemplateRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private List<String> workIds;
}
