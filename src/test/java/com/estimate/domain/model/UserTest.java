package com.estimate.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    
    @Test
    void shouldNotBeLockedWhenLockedUntilIsNull() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .build();
        
        assertFalse(user.isLocked());
    }
    
    @Test
    void shouldNotBeLockedWhenLockTimeExpired() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .lockedUntil(Instant.now().minus(1, ChronoUnit.HOURS))
                .build();
        
        assertFalse(user.isLocked());
    }
    
    @Test
    void shouldBeLockedWhenLockTimeInFuture() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .lockedUntil(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        
        assertTrue(user.isLocked());
    }
}
