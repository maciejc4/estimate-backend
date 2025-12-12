package com.estimate.adapter.out.persistence.mongodb.mapper;

import com.estimate.adapter.out.persistence.mongodb.entity.UserEntity;
import com.estimate.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .role(domain.getRole() != null ? UserEntity.Role.valueOf(domain.getRole().name()) : null)
                .companyName(domain.getCompanyName())
                .phone(domain.getPhone())
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .lockedUntil(domain.getLockedUntil())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(entity.getRole() != null ? User.Role.valueOf(entity.getRole().name()) : null)
                .companyName(entity.getCompanyName())
                .phone(entity.getPhone())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .lockedUntil(entity.getLockedUntil())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
