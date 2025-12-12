package com.estimate.domain.port.in.auth;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginUserCommand {
    String email;
    String password;
}
