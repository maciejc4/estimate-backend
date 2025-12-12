package com.estimate.adapter.out.security;

import com.estimate.domain.model.User;
import com.estimate.domain.port.out.JwtProviderPort;
import com.estimate.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProviderAdapter implements JwtProviderPort {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public String generateToken(User user) {
        return jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
    
    @Override
    public String extractUserId(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
