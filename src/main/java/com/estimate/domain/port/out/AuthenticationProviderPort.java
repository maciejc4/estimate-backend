package com.estimate.domain.port.out;

import com.estimate.domain.model.AuthenticationResult;
import com.estimate.domain.model.RegisterUserData;
import com.estimate.domain.model.UserAuthInfo;
import reactor.core.publisher.Mono;

public interface AuthenticationProviderPort {
    
    Mono<AuthenticationResult> authenticate(String email, String password);
    
    Mono<AuthenticationResult> registerUser(RegisterUserData userData);
    
    Mono<Boolean> validateToken(String token);
    
    Mono<UserAuthInfo> extractUserInfo(String token);
    
    boolean managesPasswords();
}
