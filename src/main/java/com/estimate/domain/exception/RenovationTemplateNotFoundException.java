package com.estimate.domain.exception;

public class RenovationTemplateNotFoundException extends DomainException {
    public RenovationTemplateNotFoundException(String templateId) {
        super("Renovation template not found: " + templateId);
    }
}
