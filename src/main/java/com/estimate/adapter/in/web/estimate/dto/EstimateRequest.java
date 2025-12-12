package com.estimate.adapter.in.web.estimate.dto;

import com.estimate.domain.model.EstimateWorkItem;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateRequest {
    
    @NotBlank(message = "Investor name is required")
    private String investorName;
    
    @NotBlank(message = "Investor address is required")
    private String investorAddress;
    
    private List<String> templateIds;
    private List<EstimateWorkItem> workItems;
    private BigDecimal materialDiscount;
    private BigDecimal laborDiscount;
    private String notes;
    private LocalDate validUntil;
    private LocalDate startDate;
}
