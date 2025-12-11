package com.estimate.api.controller;

import com.estimate.application.dto.EstimateRequest;
import com.estimate.application.dto.EstimateResponse;
import com.estimate.application.service.EstimateService;
import com.estimate.application.service.PdfGeneratorService;
import com.estimate.domain.model.Estimate;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
public class EstimateController {
    
    private final EstimateService estimateService;
    private final PdfGeneratorService pdfGeneratorService;
    
    @GetMapping
    public ResponseEntity<List<EstimateResponse>> getEstimates(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(estimateService.getUserEstimates(principal.getId()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EstimateResponse> getEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return ResponseEntity.ok(estimateService.getEstimate(principal.getId(), id));
    }
    
    @PostMapping
    public ResponseEntity<EstimateResponse> createEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EstimateRequest request) {
        return ResponseEntity.ok(estimateService.createEstimate(principal.getId(), request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EstimateResponse> updateEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody EstimateRequest request) {
        return ResponseEntity.ok(estimateService.updateEstimate(principal.getId(), id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        estimateService.deleteEstimate(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @RequestParam(defaultValue = "full") String detail) {
        
        Estimate estimate = estimateService.getEstimateEntity(principal.getId(), id);
        boolean fullDetail = "full".equalsIgnoreCase(detail);
        byte[] pdf = pdfGeneratorService.generatePdf(estimate, principal.getId(), fullDetail);
        
        String filename = "kosztorys_" + estimate.getInvestorName().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
