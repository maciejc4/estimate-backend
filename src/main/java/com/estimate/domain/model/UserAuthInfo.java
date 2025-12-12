package com.estimate.domain.model;

import lombok.Value;

@Value
public class UserAuthInfo {
    String userId;
    String email;
    String role;
}
