package com.estimate.domain.port.in.auth;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterUserCommand {
    String email;
    String password;
    String companyName;
    String phone;
}
