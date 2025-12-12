package com.estimate.adapter.in.web.estimate.dto;

import com.estimate.domain.model.EstimateWorkItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateResponse {
    
    private String id;
    private String investorName;
    private String investorAddress;
    private List<String> templateIds;
    private List<EstimateWorkItem> workItems;
    private BigDecimal materialDiscount;
    private BigDecimal laborDiscount;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal totalCost;
    private String notes;
    private LocalDate validUntil;
    private LocalDate startDate;
    private Instant createdAt;
    private Instant updatedAt;
}
