package com.estimate.domain.exception;

public class EstimateNotFoundException extends DomainException {
    public EstimateNotFoundException(String estimateId) {
        super("Estimate not found: " + estimateId);
    }
}
