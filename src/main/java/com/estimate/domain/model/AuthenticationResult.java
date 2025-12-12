package com.estimate.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthenticationResult {
    String token;
    String userId;
    String email;
    String role;
    AuthProviderType providerType;
}
