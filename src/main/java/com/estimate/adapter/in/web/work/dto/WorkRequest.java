package com.estimate.adapter.in.web.work.dto;

import com.estimate.domain.model.Material;
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
public class WorkRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Unit is required")
    private String unit;
    
    private List<Material> materials;
}
