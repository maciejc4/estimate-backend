package com.estimate.domain.port.out;

import com.estimate.domain.model.User;

public interface JwtProviderPort {
    
    String generateToken(User user);
    
    String extractUserId(String token);
    
    boolean validateToken(String token);
}
