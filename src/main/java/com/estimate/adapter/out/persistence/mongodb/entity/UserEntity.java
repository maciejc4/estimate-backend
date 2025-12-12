package com.estimate.adapter.out.persistence.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String passwordHash;
    
    @Builder.Default
    private Role role = Role.USER;
    
    private String companyName;
    
    private String phone;
    
    @Builder.Default
    private int failedLoginAttempts = 0;
    
    private Instant lockedUntil;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    public enum Role {
        USER, ADMIN
    }
}
