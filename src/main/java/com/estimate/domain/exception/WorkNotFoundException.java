package com.estimate.domain.exception;

public class WorkNotFoundException extends DomainException {
    public WorkNotFoundException(String workId) {
        super("Work not found: " + workId);
    }
}
