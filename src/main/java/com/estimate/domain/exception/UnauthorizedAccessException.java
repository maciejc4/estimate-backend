package com.estimate.domain.exception;

public class UnauthorizedAccessException extends DomainException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
