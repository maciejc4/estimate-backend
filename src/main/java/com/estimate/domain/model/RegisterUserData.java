package com.estimate.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterUserData {
    String email;
    String password;
    String companyName;
    String phone;
}
