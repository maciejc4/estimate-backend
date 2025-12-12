package com.estimate.domain.exception;

import java.time.Instant;

public class AccountLockedException extends DomainException {
    public AccountLockedException(Instant lockedUntil) {
        super("Account is locked until: " + lockedUntil);
    }
}
