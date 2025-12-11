package com.estimate.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstimateTest {
    
    @Test
    void shouldCalculateMaterialCostCorrectly() {
        List<EstimateWorkItem> items = new ArrayList<>();
        items.add(createWorkItem(BigDecimal.valueOf(100), BigDecimal.valueOf(50)));
        items.add(createWorkItem(BigDecimal.valueOf(200), BigDecimal.valueOf(75)));
        
        Estimate estimate = Estimate.builder()
                .workItems(items)
                .build();
        
        assertEquals(BigDecimal.valueOf(300), estimate.calculateMaterialCost());
    }
    
    @Test
    void shouldCalculateLaborCostCorrectly() {
        List<EstimateWorkItem> items = new ArrayList<>();
        items.add(createWorkItem(BigDecimal.valueOf(100), BigDecimal.valueOf(50)));
        items.add(createWorkItem(BigDecimal.valueOf(200), BigDecimal.valueOf(75)));
        
        Estimate estimate = Estimate.builder()
                .workItems(items)
                .build();
        
        assertEquals(BigDecimal.valueOf(125), estimate.calculateLaborCost());
    }
    
    @Test
    void shouldApplyMaterialDiscountCorrectly() {
        List<EstimateWorkItem> items = new ArrayList<>();
        items.add(createWorkItem(BigDecimal.valueOf(100), BigDecimal.valueOf(50)));
        
        Estimate estimate = Estimate.builder()
                .workItems(items)
                .materialDiscount(BigDecimal.valueOf(10))
                .build();
        
        assertEquals(new BigDecimal("90.00"), estimate.calculateMaterialCostWithDiscount());
    }
    
    @Test
    void shouldApplyLaborDiscountCorrectly() {
        List<EstimateWorkItem> items = new ArrayList<>();
        items.add(createWorkItem(BigDecimal.valueOf(100), BigDecimal.valueOf(100)));
        
        Estimate estimate = Estimate.builder()
                .workItems(items)
                .laborDiscount(BigDecimal.valueOf(20))
                .build();
        
        assertEquals(new BigDecimal("80.00"), estimate.calculateLaborCostWithDiscount());
    }
    
    @Test
    void shouldCalculateTotalCostWithDiscounts() {
        List<EstimateWorkItem> items = new ArrayList<>();
        items.add(createWorkItem(BigDecimal.valueOf(100), BigDecimal.valueOf(100)));
        
        Estimate estimate = Estimate.builder()
                .workItems(items)
                .materialDiscount(BigDecimal.valueOf(10))
                .laborDiscount(BigDecimal.valueOf(10))
                .build();
        
        assertEquals(new BigDecimal("180.00"), estimate.calculateTotalCost());
    }
    
    private EstimateWorkItem createWorkItem(BigDecimal materialCost, BigDecimal laborCost) {
        EstimateWorkItem item = new EstimateWorkItem();
        item.setWorkName("Test Work");
        item.setUnit("m2");
        item.setQuantity(BigDecimal.ONE);
        
        EstimateMaterialPrice materialPrice = new EstimateMaterialPrice();
        materialPrice.setMaterialName("Test Material");
        materialPrice.setUnit("kg");
        materialPrice.setPricePerUnit(materialCost);
        materialPrice.setConsumptionPerWorkUnit(BigDecimal.ONE);
        item.setMaterialPrices(List.of(materialPrice));
        
        item.setLaborPricePerUnit(laborCost);
        
        return item;
    }
}
