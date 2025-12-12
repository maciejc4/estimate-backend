package com.estimate.domain.port.in.auth;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResult {
    String token;
    String userId;
    String email;
    String role;
}
