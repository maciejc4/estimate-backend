package com.estimate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estimate {
    
    private String id;
    private String userId;
    private String investorName;
    private String investorAddress;
    @Builder.Default
    private List<String> templateIds = new ArrayList<>();
    @Builder.Default
    private List<EstimateWorkItem> workItems = new ArrayList<>();
    @Builder.Default
    private BigDecimal materialDiscount = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal laborDiscount = BigDecimal.ZERO;
    private String notes;
    private LocalDate validUntil;
    private LocalDate startDate;
    private Instant createdAt;
    private Instant updatedAt;
    
    public BigDecimal calculateMaterialCost() {
        if (workItems == null) {
            return BigDecimal.ZERO;
        }
        return workItems.stream()
                .map(EstimateWorkItem::calculateMaterialCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal calculateLaborCost() {
        if (workItems == null) {
            return BigDecimal.ZERO;
        }
        return workItems.stream()
                .map(EstimateWorkItem::calculateLaborCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal calculateMaterialCostWithDiscount() {
        BigDecimal materialCost = calculateMaterialCost();
        if (materialDiscount == null || materialDiscount.compareTo(BigDecimal.ZERO) == 0) {
            return materialCost;
        }
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                materialDiscount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );
        return materialCost.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateLaborCostWithDiscount() {
        BigDecimal laborCost = calculateLaborCost();
        if (laborDiscount == null || laborDiscount.compareTo(BigDecimal.ZERO) == 0) {
            return laborCost;
        }
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                laborDiscount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );
        return laborCost.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTotalCost() {
        return calculateMaterialCostWithDiscount().add(calculateLaborCostWithDiscount());
    }
    
    public void addWorkItem(EstimateWorkItem item) {
        if (this.workItems == null) {
            this.workItems = new ArrayList<>();
        }
        this.workItems.add(item);
    }
    
    public void applyMaterialDiscount(BigDecimal discount) {
        this.materialDiscount = discount;
    }
    
    public void applyLaborDiscount(BigDecimal discount) {
        this.laborDiscount = discount;
    }
}
